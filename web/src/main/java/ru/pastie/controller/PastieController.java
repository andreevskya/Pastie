package ru.pastie.controller;

import com.threecrickets.jygments.Jygments;
import com.threecrickets.jygments.ResolutionException;
import com.threecrickets.jygments.contrib.HtmlFormatterEx;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import org.springframework.web.bind.annotation.ResponseBody;
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

    private Logger logger = LogManager.getLogger(PastieController.class);
    
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
    
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public void internalServerError(RuntimeException ex) {
        logger.error(ex);
    }
    
    @RequestMapping(value="/paste/{id}", method=RequestMethod.GET)
    public String viewPaste(@PathVariable("id") String id, Model model, HttpServletRequest request, HttpServletResponse response) {
        Paste paste = getPaste(id, request, response);
        model.addAttribute("paste", paste);
        
        HtmlFormatterEx formatter;
        com.threecrickets.jygments.grammar.Lexer lexer;
        try {
            formatter = new HtmlFormatterEx();
            if(paste.getLexer() != Lexer.PLAIN) {
                lexer = com.threecrickets.jygments.grammar.Lexer.getByName(paste.getLexer().toString().toLowerCase());
            } else {
                lexer = new com.threecrickets.jygments.grammar.Lexer();
            }
        } catch(ResolutionException rex) {
            throw new RuntimeException(rex);
        }
        
        StringWriter sw = new StringWriter();
        try {
            formatter.getStyleSheet(sw);
        } catch(IOException ioex) {
            throw new RuntimeException(ioex);
        }
        sw.flush();
        
        model.addAttribute("css", sw.toString());
        sw = new StringWriter();
        try {
            Jygments.highlight(paste.getPaste(), lexer, formatter, sw);
        } catch(IOException ioex) {
            throw new RuntimeException(ioex);
        }
        sw.flush();
        
        model.addAttribute("formattedPaste", sw.toString());
        return "paste";
    }

    @RequestMapping(value="/raw/{id}", method=RequestMethod.GET)
    @ResponseBody
    public String raw(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response) {
        Paste p = getPaste(id, request, response);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        return p.getPaste();
    }

    @RequestMapping(value="/latest", method=RequestMethod.GET)
    public String latest(Model model) {
        List<Paste> latest = service.getLatest();
        model.addAttribute("nav_latest", true);
        model.addAttribute("latest", latest);
        return "latest";
    }

    @RequestMapping(value="/top", method=RequestMethod.GET)
    public String top(Model model) {
        List<Paste> top = service.getTop();
        model.addAttribute("nav_top", true);
        model.addAttribute("top", top);
        return "popular";
    }

    @RequestMapping(value="/all", method=RequestMethod.GET)
    public String all(Model model) {
        return all(1, model);
    }

    @RequestMapping(value="/all/{page}", method=RequestMethod.GET)
    public String all(@PathVariable("page")int page, Model model) {
        if(page <= 0)
            page = 1;
        int pages = service.countPublicAndNotExpiredPages();
        List<Paste> pageContent = service.listPublicAndNotExpired(page);
        model.addAttribute("nav_all");
        model.addAttribute("currentPage", page);
        model.addAttribute("numPages", pages);
        model.addAttribute("page", pageContent);
        return "all";
    }

    @RequestMapping(value="/about", method=RequestMethod.GET)
    public String about(Model model) {
        model.addAttribute("nav_about", true);
        return "about";
    }
    
    private Paste getPaste(String id, HttpServletRequest request, HttpServletResponse response) throws NoSuchPasteException {
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
        return paste;
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
