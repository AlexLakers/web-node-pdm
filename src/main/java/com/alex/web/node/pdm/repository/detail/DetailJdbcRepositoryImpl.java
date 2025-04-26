package com.alex.web.node.pdm.repository.detail;

import com.alex.web.node.pdm.dto.detail.DetailDto;
import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.alex.web.node.pdm.dto.detail.DetailDto.*;

/**
 * This class includes functional which implements additional repository-interface {@link DetailJdbcRepository detailJdbcRepository}.
 */

@RequiredArgsConstructor
public class DetailJdbcRepositoryImpl implements DetailJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<DetailDto> findDetailsByIds(List<Long> ids) {
        //my alternative variant
        /*SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        String SQL_FIND_ALL_BY_IDS = """
            SELECT id, name, amount, specification_id
            FROM detail WHERE id IN (:ids);
            """;*/

        final String sqlFindAllByIds = """
                SELECT id,name,amount,specification_id
                FROM detail WHERE id IN (SELECT id FROM detail_tmp);
                """;
        final String sqlCreateTmp = """
                CREATE TABLE IF NOT EXISTS detail_tmp (
                id BIGINT NOT NULL);
                """;
        List<Object[]> params = ids.stream().map(id -> new Object[]{id}).collect(Collectors.toList());
        jdbcTemplate.execute(sqlCreateTmp);
        jdbcTemplate.batchUpdate("INSERT INTO detail_tmp(id) VALUES (?)", params);

        List<DetailDto> detailDtoList = jdbcTemplate.query(
                sqlFindAllByIds,
                (rs, rowNum) -> new DetailDto(rs.getLong(Fields.id),
                        rs.getString(Fields.name),
                        rs.getInt(Fields.amount),
                        rs.getLong("specification_id"))


        );
        jdbcTemplate.update("DELETE FROM detail_tmp");
        return detailDtoList;
    }

    public List<DetailDto> findDetailsBySpecificationId(Long specificationId) {
        final String sqlFindAllBySpecId = """
                SELECT id,name,amount,specification_id
                FROM detail WHERE specification_id=?
                """;
        return jdbcTemplate.query(sqlFindAllBySpecId,
                (rs, rowCount) -> new DetailDto(
                        rs.getLong(Fields.id),
                        rs.getString(Fields.name),
                        rs.getInt(Fields.amount),
                        rs.getLong("specification_id")),
                specificationId
        );
    }

    public Page<DetailDto> findDetailsBy(Pageable pageable) {
        String orderColumns = pageable.getSort()
                .stream().map(Sort.Order::getProperty)
                .collect(Collectors.joining(", "));
        String sqlCount = """
                       SELECT COUNT(id) FROM detail
                """;
        String sqlPage = "SELECT id,name,amount,specification_id FROM detail ORDER BY "+ orderColumns + " LIMIT ? OFFSET ?";

        int countRows = jdbcTemplate.queryForObject(sqlCount, Integer.class);

        List<DetailDto> content = jdbcTemplate.query(sqlPage,
                (rs, rowNum) -> new DetailDto(
                        rs.getLong(Fields.id),
                        rs.getString(Fields.name),
                        rs.getInt(Fields.amount),
                        rs.getLong("specification_id")
                ),/*orderColumns,*/ pageable.getPageSize(), pageable.getOffset()

        );
        return new PageImpl<>(content, pageable, countRows);
    }

    @Override
    public boolean existsDetailByName(String name) {
        String sqlExists= """
                SELECT EXISTS(SELECT 1 FROM detail WHERE name=?)
                """;
        return jdbcTemplate.queryForObject(sqlExists,Boolean.class,name);
    }

    @Override
    public Optional<DetailDto> findDetailById(Long id) {
        final String sqlFindById = """
                SELECT id,name,amount,specification_id
                FROM detail WHERE id=?
                """;
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    sqlFindById,
                    (rs, rowCount) -> new DetailDto(
                            rs.getLong(Fields.id),
                            rs.getString(Fields.name),
                            rs.getInt(Fields.amount),
                            rs.getLong("specification_id")
                    ),
                    id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }


    }

    public DetailDto saveDetail(NewDetailDto newDetailDto) {
        final String sqlSave = """
                INSERT INTO detail(name,amount,specification_id) VALUES (?,?,?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sqlSave, new String[]{"id", "name", "amount", "specification_id"});
                    ps.setObject(1, newDetailDto.name());
                    ps.setObject(2, newDetailDto.amount());
                    ps.setObject(3, newDetailDto.specificationId());
                    return ps;
                },
                keyHolder

        );
        //or return only id and use findById method for find and map detail dto
        return map(keyHolder);
    }

    public boolean deleteDetail(Long id) {
        final String sqlDelete = """
                DELETE FROM detail WHERE id=?
                """;
        return jdbcTemplate.update(sqlDelete, id) > 0;

    }

    public DetailDto updateDetail(Long id, UpdateDetailDto updateDetailDto) {
        final String sqlUpdate = """
                UPDATE detail
                SET name=?,amount=?
                WHERE id=?
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sqlUpdate, Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1, updateDetailDto.name());
                    ps.setObject(2, updateDetailDto.amount());
                    ps.setObject(3, id);
                    return ps;
                },
                keyHolder);
        return map(keyHolder);
    }

    private DetailDto map(KeyHolder keyHolder) {
        return keyHolder.getKeyList().stream()
                .map(map -> new DetailDto(
                        (Long) map.get(Fields.id),
                        (String) map.get(Fields.name),
                        (Integer) map.get(Fields.amount),
                        (Long) map.get("specification_id")
                )).findFirst().orElseThrow();
    }


}

