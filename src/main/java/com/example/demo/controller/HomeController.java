package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Podcast;
import com.example.demo.repository.PodcastRepository;
import com.example.demo.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private ClientService clientService;

    private Client getClientData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            User user = (User) authentication.getPrincipal();
            Client client = clientService.getClientByNickname(user.getUsername());
            return client;
        }

        return null;
    }

    @GetMapping(value = "/")
    @PreAuthorize("isAuthenticated()")
    public String allPodcasts(Model model){
        List<Podcast> podcastList = podcastRepository.findAll();
        podcastList.sort((a,b)-> (int) (a.getId() - b.getId()));
        model.addAttribute("podcastList", podcastList);

        Podcast podcast = new Podcast();
        model.addAttribute("newPodcast", podcast);

        model.addAttribute("currentClient", getClientData());
        return "main";
    }


    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String savePodcast(@ModelAttribute("newPodcast") Podcast podcast) {
        podcastRepository.save(podcast);

        return "redirect:/";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updatePodcast(@PathVariable Long id,
                                @ModelAttribute("podcast")Podcast currentPodcast,
                                Model model){
        Podcast podcast = podcastRepository.findById(id).orElse(null);

        podcast.setId(id);
        podcast.setTitle(currentPodcast.getTitle());
        podcast.setDescription(currentPodcast.getDescription());

        podcastRepository.save(podcast);

        List<Podcast> podcastList = podcastRepository.findAll();
        podcastList.sort((a,b)-> (int) (a.getId() - b.getId()));
        model.addAttribute("podcastList", podcastList);

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editPodcast(@PathVariable Long id, Model model) {
        model.addAttribute("podcast",podcastRepository.findById(id).get());
        model.addAttribute("currentClient", getClientData());

        List<Podcast> podcastList = podcastRepository.findAll();
        podcastList.sort((a,b)-> (int) (a.getId() - b.getId()));
        model.addAttribute("podcastList", podcastList);
        return "edit";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deletePodcast(@PathVariable Long id, Model model) {
        model.addAttribute("currentClient", getClientData());
        podcastRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("currentClient", getClientData());
        return "403";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("currentClient", getClientData());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("currentClient", getClientData());
        return "registration";
    }

    @PostMapping("/register")
    public String forRegister(@RequestParam(name = "user_nickname") String nickname,
                              @RequestParam(name = "user_password") String password,
                              @RequestParam(name = "re_user_password") String rePassword) {
        if (password.equals(rePassword)){
            Client newClient = new Client();
            newClient.setPassword(password);
            newClient.setNickname(nickname);
            if (clientService.createClient(newClient) != null) {
                return "redirect:/";
            }
        }
        return "redirect:/register?error";
    }
}
