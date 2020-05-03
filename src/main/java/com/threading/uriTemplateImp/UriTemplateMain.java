package com.threading.uriTemplateImp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.util.UriTemplate;

public class UriTemplateMain {

    public UriTemplateMain()
    {}

    public void resolveUri(UriTemplateMain m)
    {
        List variable;
        UriTemplate uri = new UriTemplate("https://{HOST}/ui/");
        //System.out.println(uri.getVariableNames());
        variable = uri.getVariableNames();

        //System.out.println(splitted);
        int i = 0;
        while (i < variable.size()) {
            String var = (String)variable.get(i);
            String[] splitted = var.split("\\.");
            System.out.println(splitted[0]);
            System.out.println(splitted[1]);
            i++;
        }

    }

    public String resolveVariableValues(String variable){
        String[] splitted = variable.split("\\.");
        return "1";
    }

    public static void main(String[] args){
        UriTemplateMain m = new UriTemplateMain();
        m.resolveUri(m);
    }
}
