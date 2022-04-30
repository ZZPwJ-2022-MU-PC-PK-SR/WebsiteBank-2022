package pl.zzpwj_2022_mu_pc_pk_sr.websitebank2022;

import org.springframework.web.bind.annotation.RequestMapping;

public class IndexController {
    @RequestMapping({"/",""})
    public String index(){
        return "index";
    }
}
