package io.github.xtemplus.utils;

import io.github.xtemplus.service.LicenseValidator;

import java.util.Scanner;

/**
 * 许可证生成工具类
 * 用于生成许可证密钥，可以在单独的工具类中调用
 *
 * @author template
 */
public class LicenseGenerator {

    /**
     * 生成许可证密钥
     *
     * @param expireDate 过期日期（格式：yyMMdd，例如：250101）
     * @return 加密后的许可证密钥
     */
    public static String generate(String expireDate) {
        return LicenseValidator.generateLicense(expireDate);
    }

    /**
     * 主方法，支持多种运行方式：
     * 1. 直接运行：会提示输入过期时间
     * 2. 命令行参数：java LicenseGenerator 20251101
     */
    public static void main(String[] args) {
        String expireDate;
        Scanner scanner = null;

        try {
            // 方式1：命令行参数（过期时间）
            if (args.length >= 1) {
                expireDate = args[0];
            }
            // 方式2：无命令行参数，从控制台读取
            else {
                scanner = new Scanner(System.in);
                System.out.println("========================================");
                System.out.println("许可证生成工具");
                System.out.println("========================================");
                System.out.print("请输入过期日期（格式：yyMMdd，例如：250101）: ");
                expireDate = scanner.nextLine().trim();
                
                if (expireDate.isEmpty()) {
                    System.err.println("过期日期不能为空！");
                    return;
                }
            }

            // 生成许可证
            String licenseKey = generate(expireDate);
            String machineCode = MachineCodeUtil.getMachineCode();
            
            System.out.println("=========================================");
            System.out.println("许可证生成成功！");
            System.out.println("=========================================");
            System.out.println("机器码: " + machineCode);
            System.out.println("过期时间: " + expireDate + " 00:00:00 (格式: yyMMdd)");
            System.out.println("=========================================");
            System.out.println("许可证密钥（请复制到配置文件中）:");
            System.out.println(licenseKey);
            System.out.println("=========================================");
            System.out.println();
            System.out.println("在 application.yml 或 application.properties 中配置:");
            System.out.println("template.core.license-key=" + licenseKey);
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("生成许可证失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
}

