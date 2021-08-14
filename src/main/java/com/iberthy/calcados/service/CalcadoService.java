package com.iberthy.calcados.service;

import com.iberthy.calcados.models.Calcado;
import com.iberthy.calcados.repositorys.CalcadoRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class CalcadoService {
    CalcadoRepository repository;

    @Autowired
    public void setRepository(CalcadoRepository repository){
        this.repository = repository;
    }

    public void salvar(Calcado calcado){
        this.repository.save(calcado);
        System.out.println(calcado);
    }

    public Calcado buscarPorId(Long id){
        var calcado = this.repository.getById(id);
        System.out.println(calcado);
        return calcado;
    }

    public void deletar(Long id){
        var calcado = this.repository.getById(id);
        calcado.setDelete(new Date());
        this.repository.save(calcado);
        System.out.println(calcado);
    }

    public List<Calcado> listarTodosNaoDeletados(){

        List<Calcado> resultado = new ArrayList<>();

        for (Calcado calcado : this.repository.findAll()) {
            if(calcado.getDelete() == null){ resultado.add(calcado); }
        }

        return resultado;
    }
}
