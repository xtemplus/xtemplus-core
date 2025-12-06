package io.github.xtemplus.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

/**
 * YAML配置文件加载工厂类
 * 用于支持@PropertySource注解加载YAML格式的配置文件
 *
 * @author template
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());
        if (!sources.isEmpty()) {
            return sources.get(0);
        }
        return super.createPropertySource(name, resource);
    }
}





