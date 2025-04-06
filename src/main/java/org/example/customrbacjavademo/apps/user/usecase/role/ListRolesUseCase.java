package org.example.customrbacjavademo.apps.user.usecase.role;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.RoleResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.RoleJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.role.mappers.RoleMapper;
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
public class ListRolesUseCase {
  private final RoleJpaRepository repository;

  public ListRolesUseCase(final RoleJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public Pagination<RoleResponse> execute(final SearchQuery searchQuery) {
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
        pageResult.map(RoleMapper::jpaToEntity).map(RoleMapper::entityToResponse).toList()
    );
  }

  private Specification<RoleJpaRepository> assembleSpecification(final String terms) {
    final var fields = List.of("name", "description");
    return SpecificationUtils.likeMultiple(fields, terms);
  }
}
