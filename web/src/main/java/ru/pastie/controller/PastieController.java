package ru.pastie.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.pastie.exceptions.NoSuchPasteException;
import ru.pastie.om.Expiration;
import ru.pastie.om.Lexer;
import ru.pastie.om.Paste;
import ru.pastie.om.PasteForm;
import ru.pastie.service.PasteService;

@Controller
public class PastieController {
    
    @Autowired
    PasteService service;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("form", new PasteForm());
        fillIndexModel(model);
        return "index";
    }
    
    private Model fillIndexModel(Model model) {
        List<Paste> latest = service.getLatest();
        model.addAttribute("maxLatest", service.getMaxLatestPastes());
        model.addAttribute("latest", latest);
        model.addAttribute("expirationList", Expiration.values());
        model.addAttribute("syntaxList", Lexer.values());
        return model;
    }
    
    @RequestMapping(value="/", method=RequestMethod.POST)
    public String paste(
            @ModelAttribute("form") @Valid PasteForm form,
            BindingResult result,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) {
        if(result.hasErrors()) {
            model.addAttribute("form", form);
            fillIndexModel(model);
            return "index";
        }
        Paste paste = form.toPaste();
        paste.setUserIp(request.getRemoteAddr());
        paste.setUserAgent(request.getHeader("User-Agent"));
        paste = service.save(paste);
        markAsViewed(request, paste.getId());
        return "redirect:/paste/" + paste.getId();
    }
    
    @ResponseStatus(value=HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchPasteException.class)
    public void noPaste() {}
    
    @RequestMapping(value="/paste/{id}", method=RequestMethod.GET)
    public String viewPaste(@PathVariable("id") String id, Model model, HttpServletRequest request, HttpServletResponse response) {
        Paste paste = service.findOne(id);
        if( paste == null) {
            throw new NoSuchPasteException();
        }
        if( paste.isExpired()) {
            throw new NoSuchPasteException();
        }
        if(!hasViewed(request, paste.getId())) {
            paste.setNumViews(paste.getNumViews() + 1);
            service.save(paste);
            markAsViewed(request, paste.getId());
        }
        
        model.addAttribute("paste", paste);
        return "paste";
    }
    
    
    private void markAsViewed(HttpServletRequest request, String id) {
        HttpSession session = request.getSession(true);
        List<String> viewedPasties = (List<String>)session.getAttribute("viewed");
        if(viewedPasties == null) {
            session.setAttribute("viewed", new ArrayList<String>());
            viewedPasties = (List<String>)session.getAttribute("viewed");
        }
        if(!viewedPasties.contains(id)) {
            viewedPasties.add(id);
        }
    }
    
    private boolean hasViewed(HttpServletRequest request, String id) {
        HttpSession session = request.getSession(true);
        List<String> viewedPasties = (List<String>)session.getAttribute("viewed");
        if( viewedPasties == null)
            return false;
        return viewedPasties.contains(id);
    }
}
