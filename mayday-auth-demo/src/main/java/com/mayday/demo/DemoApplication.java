package com.mayday.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 权限模块演示应用启动类
 * <p>
 * 用于测试和演示 mayday-auth 权限模块的功能。
 * 通过 {@code scanBasePackages} 扫描核心权限模块的组件。
 * </p>
 *
 * @author MayDay Auth Generator
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {"com.mayday.auth", "com.mayday.demo"})
@MapperScan("com.mayday.auth.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("========================================");
        System.out.println("  MayDay Auth Demo Started Successfully!");
        System.out.println("========================================");
    }
}
