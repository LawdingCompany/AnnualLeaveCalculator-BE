package com.lawding.leavecalc.domain.feedback.service;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

class FeedbackServiceTest {
    public static void main(String[] args) throws Exception {
        // JDBC URL (iam 플러그인 명시)
        String url = "jdbc:mysql:aws://lawdingdb.cdsmiu2m0dc8.ap-northeast-2.rds.amazonaws.com:3306/leave_calculator?wrapperPlugins=iam";

        Properties props = new Properties();
        props.setProperty("user", "leavecalc_ec2");
        // ⚠️ 비밀번호 절대 넣지 마세요 (IAM 모드니까)

        System.out.println("Connecting...");
        try (Connection conn = DriverManager.getConnection(url, props)) {
            System.out.println("✅ Connected! DB version: " + conn.getMetaData().getDatabaseProductVersion());
        }
    }
}