package cyou.arfsd.spendbackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cyou.arfsd.spendbackend.Utils.AuthMiddleware;

@Configuration
public class Config implements WebMvcConfigurer {

    @Autowired
    private AuthMiddleware authMiddleware;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authMiddleware).addPathPatterns("/api/v1/**");
    }
}