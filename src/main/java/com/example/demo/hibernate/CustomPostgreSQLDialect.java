package com.example.demo.hibernate;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.DoubleType;

public class CustomPostgreSQLDialect extends PostgreSQLDialect {

  public CustomPostgreSQLDialect() {
    super();
    registerFunction(
        "calculate_cosine_similarity",
        new StandardSQLFunction("calculate_cosine_similarity", DoubleType.INSTANCE));
  }
}
