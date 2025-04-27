package org.example.customrbacjavademo;

import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.List;

public class DatabaseCleanUpExtension implements BeforeEachCallback {
  @Override
  public void beforeEach(final ExtensionContext context) {
    final var applicationContext = SpringExtension.getApplicationContext(context);

    cleanUp(List.of(
        applicationContext.getBean(UserJpaRepository.class),
        applicationContext.getBean(RoleJpaRepository.class),
        applicationContext.getBean(PermissionJpaRepository.class)
    ));
  }

  private void cleanUp(final Collection<CrudRepository> repositories) {
    repositories.forEach(CrudRepository::deleteAll);
  }
}
