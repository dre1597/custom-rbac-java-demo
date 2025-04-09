package org.example.customrbacjavademo.apps.user.usecase.user;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.UserResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.UserJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.user.mappers.UserMapper;
import org.example.customrbacjavademo.common.domain.helpers.Pagination;
import org.example.customrbacjavademo.common.domain.helpers.SearchQuery;
import org.example.customrbacjavademo.common.domain.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class ListUsersUseCase {
  private final UserJpaRepository repository;

  public ListUsersUseCase(final UserJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public Pagination<UserResponse> execute(final SearchQuery searchQuery) {
    final var page = PageRequest.of(
        searchQuery.page(),
        searchQuery.perPage(),
        Sort.by(Sort.Direction.fromString(searchQuery.direction()), searchQuery.sort())
    );

    final var where = Optional.ofNullable(searchQuery.terms())
        .filter(str -> !str.isBlank())
        .map(this::assembleSpecification)
        .orElse(null);

    final var pageResult = this.repository.findAll(where(where), page);

    return new Pagination<>(
        pageResult.getNumber(),
        pageResult.getSize(),
        pageResult.getTotalElements(),
        pageResult.map(UserMapper::jpaToEntity).map(UserMapper::entityToResponse).toList()
    );
  }

  private Specification<UserJpaRepository> assembleSpecification(final String terms) {
    final var fields = List.of("name");
    return SpecificationUtils.likeMultiple(fields, terms);
  }
}
