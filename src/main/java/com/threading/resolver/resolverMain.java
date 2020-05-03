package com.threading.resolver;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class resolverMain {
    private final ObjectMapper mapper;
    ResourcePatternResolver resolver;
    String prefix;
    String cl;
    public resolverMain(@Value("collectors") String prefix) throws IOException
    {
        System.out.println(prefix);
        this.prefix = prefix;
        this.mapper = new ObjectMapper();
        resolver = new PathMatchingResourcePatternResolver();
        //cl = "classpath:%s/*.json";
        //ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        //Resource[] resources = resolver.getResources("classpath*:/*.xml") ;
    }

public String getResolver() throws IOException
{
    cl = "classpath:%s/*.json";
    Resource[] resources = resolver.getResources("classpath:collectors/*.json") ;

    System.out.println(resources);
    for(Resource file: resources)
    {
        System.out.println(file);
        CollectorConfiguration conf = mapper.readValue(file.getInputStream(), new TypeReference<CollectorConfiguration>(){});
        System.out.println(conf);
        System.out.println(conf.getName());
        conf.getProcessors().forEach(pro -> {
            System.out.println(pro.getName());
            System.out.println(pro.getArguments());
        });
        System.out.println(conf.getArguments());
    }



    return this.prefix;
}

public void test()
{

}
}
