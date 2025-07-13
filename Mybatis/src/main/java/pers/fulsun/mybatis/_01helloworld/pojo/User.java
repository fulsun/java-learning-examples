package pers.fulsun.mybatis._01helloworld.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private Integer id;
    private String name;
    private Integer age;
    // Getter/Setter 省略
}