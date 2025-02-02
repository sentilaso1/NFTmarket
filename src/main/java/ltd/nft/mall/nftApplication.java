package ltd.nft.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("ltd.nft.mall.dao")
@SpringBootApplication
public class nftApplication {
    public static void main(String[] args) {
        SpringApplication.run(nftApplication.class, args);
    }
}
