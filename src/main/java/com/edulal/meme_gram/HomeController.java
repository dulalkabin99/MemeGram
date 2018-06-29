package com.edulal.meme_gram;



import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {


    @Autowired
    ContentRepo contentRepo;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String contentList(Model model) {
        model.addAttribute("contents", contentRepo.findAll());
        return "list";

    }

    @GetMapping("/add")
    public String messageForm(Model model) {
        model.addAttribute("memeGram", new MemeGram());
        return "addform";
    }

    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute("memeGram") MemeGram memeGram,
                              BindingResult result, @RequestParam("file")MultipartFile file) {
        if (result.hasErrors()) {
            return "addform";
        }

        else if (file.isEmpty()) {
            return "redirect:/add";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            memeGram.setHeadshot(uploadResult.get("url").toString());
            contentRepo.save(memeGram);
        }
        catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";

        }
        //contentRepo.save(memeGram);
        return "redirect:/";

    }


    @RequestMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", contentRepo.findById(id).get());
        return "detail";
    }

    @RequestMapping("/update/{id}")
    public String updateContent(@PathVariable("id") long id, Model model) {
        model.addAttribute("memeGram", contentRepo.findById(id));
        return "addform";
    }


        @RequestMapping("/delete/{id}")
        public String deleteMeme(@PathVariable("id")long id){
            contentRepo.deleteById(id);
            return "redirect:/";

    }

}
