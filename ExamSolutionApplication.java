package com.exam.exam_solution;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@SpringBootApplication
public class ExamSolutionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ExamSolutionApplication.class, args);
    }

    @Override
    public void run(String... args) {
        RestTemplate restTemplate = new RestTemplate();
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> body = Map.of(
            "name", "Gaurav Luthra",
            "regNo", "2210990317",
            "email", "gaurav317.be22@chitkara.edu.in"
        );
        ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, body, Map.class);
        System.out.println("Response from generateWebhook: " + response.getBody());
        String webhook = (String) response.getBody().get("webhook");
        String token = (String) response.getBody().get("accessToken");
        System.out.println("Webhook: " + webhook);
        System.out.println("Token: " + token);
        if (webhook == null || token == null) {
            System.err.println("Failed to get webhook or token. Exiting.");
            return;
        }
        String finalQuery = "SELECT p.AMOUNT AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                "ORDER BY p.AMOUNT DESC LIMIT 1;";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, String> finalBody = Map.of("finalQuery", finalQuery);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(finalBody, headers);
        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhook, entity, String.class);
        System.out.println("Submit Response: " + submitResponse.getStatusCode());
        System.out.println("Submit Response Body: " + submitResponse.getBody());
        System.out.println("Solution submitted successfully!");
    }
}
