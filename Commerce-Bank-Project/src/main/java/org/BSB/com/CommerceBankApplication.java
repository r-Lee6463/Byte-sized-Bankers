/*  Team Byte-sized Bankers / Made in conjecture with ChatGPT model o4-mini-high
 *  
 *  Current state of LLM: Is able to hold onversation in the webpage and
 *  receives the commands to add transaction and create/delete goals
 *  but changes do not persist to the database.
 */
package org.BSB.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommerceBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommerceBankApplication.class, args);
    }
}