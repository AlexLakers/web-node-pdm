package com.alex.web.node.pdm.controller;

import com.alex.web.node.pdm.dto.detail.NewDetailDto;
import com.alex.web.node.pdm.dto.detail.UpdateDetailDto;
import com.alex.web.node.pdm.service.DetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RequestMapping("/details")
@RequiredArgsConstructor
@Controller
public class DetailController {
    private final DetailService detailService;

    @GetMapping("/{id}")
    public String findById(Model model,
                           @PathVariable Long id,
                           @RequestHeader String referer) {
        model.addAllAttributes(Map.of("detail",detailService.findById(id),"referer",referer));
        return "detail/detail";
    }

    @PostMapping
    public String create(Model model,
                         @Validated NewDetailDto newDetailDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors())
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

        else
            model.addAttribute("detail", detailService.create(newDetailDto));

        return "redirect:/specifications/" + newDetailDto.specificationId() + "/details";
    }

    @PostMapping("/{specId}/delete")
    public String delete(@RequestParam("detailId") Long id,
                         @PathVariable Long specId) {
        detailService.delete(id);
        return "redirect:/specifications/{specId}/details";
    }

    @PostMapping("/{id}/update")
    public String update(Model model,
                         @PathVariable Long id,
                         @Validated UpdateDetailDto updateDetailDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/details/{id}";
        }
        model.addAttribute("detail", detailService.update(id, updateDetailDto));
        return "detail/detail";
    }

}
