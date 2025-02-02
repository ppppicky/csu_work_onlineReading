package org.example.config;


import com.github.pagehelper.PageInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan("org.example.mapper")
public class MyBatisConfig {

    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 设置分页插件参数
        properties.setProperty("helperDialect", "mysql"); // 数据库方言，适配 MySQL
        properties.setProperty("reasonable", "true");     // 分页合理化，防止页码超出范围
        properties.setProperty("supportMethodsArguments", "true"); // 支持通过 Mapper 方法参数传递分页参数
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    /**
     * 配置 SqlSessionFactory，注入分页插件
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, PageInterceptor pageInterceptor) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        // 添加分页插件
        sqlSessionFactoryBean.setPlugins(pageInterceptor);

        return sqlSessionFactoryBean.getObject();
    }
}
