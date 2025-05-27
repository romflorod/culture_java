package com.app.culture.controller;

import com.app.culture.model.Anime;
import com.app.culture.service.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    @GetMapping("/searchanime")
    public String searchAnimePage() {
        return "searchanime";
    }

    @PostMapping("/searchanime")
    public String searchAnime(@RequestParam String query, Model model) {
        List<Anime> animes = animeService.fetchAndSaveAnimes(query);
        model.addAttribute("animes", animes);
        return "searchanime";
    }
}