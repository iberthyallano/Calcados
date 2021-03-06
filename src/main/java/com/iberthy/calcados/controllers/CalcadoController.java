package com.iberthy.calcados.controllers;

import com.iberthy.calcados.mensage.Mensagem;
import com.iberthy.calcados.models.Calcado;
import com.iberthy.calcados.service.CalcadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CalcadoController {

    CalcadoService service;

    @Autowired
    public void setService(CalcadoService service) { this.service = service; }

    public List<Calcado> ManipulaCarrinho(HttpServletRequest request, HttpServletResponse response, Long id, boolean remove){

        HttpSession session = request.getSession();
        List<Calcado> carrinho = (List<Calcado>) session.getAttribute("carrinho");

        if (carrinho == null) { carrinho = new ArrayList<>(); }

        if(id != null && remove){
            var calcado = this.service.buscarPorId(id);
            System.out.println(calcado);
            carrinho.remove(calcado);
        }else if(id != null){
            var calcado = this.service.buscarPorId(id);
            System.out.println(calcado);
            carrinho.add(calcado);
        }

        System.out.println(carrinho);
        session.setAttribute("carrinho", carrinho);

        return carrinho;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String homePage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException{

        var carrinho = ManipulaCarrinho(request, response, null, false);
        var calcados = this.service.listarTodosNaoDeletados();

        model.addAttribute("calcados", calcados);
        model.addAttribute("carrinho", carrinho);

        var dataDeAcessoAoSite = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss");
        var dataFormatada = formatter.format(dataDeAcessoAoSite);
        Cookie cookie = new Cookie("visita", URLEncoder.encode(dataFormatada, "UTF-8"));
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        return "index";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String visualizarEstoque(Model model){
        var calcados = this.service.listarTodosNaoDeletados();
        model.addAttribute("calcados", calcados);
        return "admin";
    }

    @RequestMapping(value = "/cadastrar", method = RequestMethod.GET)
    public String cadastrarProduto(Model model){
        model.addAttribute("calcado", new Calcado());
        model.addAttribute("edicao", false);
        return "cadastro";
    }

    @RequestMapping(value = "/salvar", method = RequestMethod.POST)
    public String salvarProduto(Model model, @ModelAttribute @Valid Calcado calcado, Errors errors, RedirectAttributes redirectAttributes){
        if (errors.hasErrors()){
            //Mandar os erros para o formul??rio
            model.addAttribute("calcado",calcado);
            model.addAttribute("edicao", false);
            return "cadastro";
        }else{
            try{
                this.service.salvar(calcado);
                redirectAttributes.addAttribute("mensagem", Mensagem.SUCESSO_AO_REALIZAR_UMA_ACAO);
            }catch (Exception exeption){
                redirectAttributes.addAttribute("mensagem", Mensagem.ERRO_AO_REALIZAR_UMA_ACAO);
            }
            return "redirect:/admin";
        }
    }


    @RequestMapping(value = "/produto/{id}", method = RequestMethod.GET)
    public String visualizarProduto(@PathVariable(name = "id") Long id, Model model,HttpServletRequest request, HttpServletResponse response){
        var calcado = service.buscarPorId(id);
        var carrinho = ManipulaCarrinho(request, response, null, false);


        model.addAttribute("calcado", calcado);
        model.addAttribute("carrinho", carrinho);

        return "produto";
    }

    @RequestMapping(value = "/editar/{id}", method = RequestMethod.GET)
    public ModelAndView editarProduto(@PathVariable(name = "id") Long id){
        ModelAndView modelAndView = new ModelAndView("cadastro");
        var calcado = service.buscarPorId(id);
        modelAndView.addObject("calcado", calcado);
        modelAndView.addObject("edicao", true);
        return modelAndView;
    }

    @RequestMapping(value = "/deletar/{id}", method = RequestMethod.GET)
    public String deletarProduto(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes){
        try{
            this.service.deletar(id);
            redirectAttributes.addAttribute("mensagem", Mensagem.SUCESSO_AO_REALIZAR_UMA_ACAO);
        }catch (Exception exeption){
            redirectAttributes.addAttribute("mensagem", Mensagem.ERRO_AO_REALIZAR_UMA_ACAO);
        }

        return "redirect:/admin";
    }


    @RequestMapping(value = "/adicionarAoCarrinho/{id}", method = RequestMethod.GET)
    public String adicionarAoCarrinho(@PathVariable(name = "id") Long id, HttpServletRequest request, HttpServletResponse response){
        ManipulaCarrinho(request, response, id, false);
        return "redirect:/";
    }

    @RequestMapping(value = "/removerDoCarrinho/{id}", method = RequestMethod.GET)
    public String removerDoCarrinho(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request, HttpServletResponse response){
        var carrinho = ManipulaCarrinho(request, response, id, true);
        model.addAttribute("carrinho", carrinho);
        return "carrodecompras";
    }

    @RequestMapping(value = "/visualizarCarrinho", method = RequestMethod.GET)
    public String visualizarCarrinho(Model model, HttpServletRequest request, HttpServletResponse response){
        var carrinho =  ManipulaCarrinho(request, response, null, false);

        if(carrinho.size() == 0){
            return "redirect:/";
        }

        model.addAttribute("carrinho", carrinho);
        return "carrodecompras";
    }

    @RequestMapping(value = "/finalizarCompra", method = RequestMethod.GET)
    public String finalizarCompra(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/";
    }
}
