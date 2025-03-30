package org.example.customrbacjavademo.apps.user.usecase;

import org.example.customrbacjavademo.apps.user.infra.api.dto.responses.PermissionResponse;
import org.example.customrbacjavademo.apps.user.infra.persistence.PermissionJpaRepository;
import org.example.customrbacjavademo.apps.user.usecase.mappers.PermissionMapper;
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
public class ListPermissionsUseCase {
  private final PermissionJpaRepository repository;

  public ListPermissionsUseCase(final PermissionJpaRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  public Pagination<PermissionResponse> execute(final SearchQuery searchQuery) {
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
        pageResult.map(PermissionMapper::jpaToEntity).map(PermissionMapper::entityToResponse).toList()
    );
  }

  private Specification<PermissionJpaRepository> assembleSpecification(final String terms) {
    var fields = List.of("name", "scope", "description");
    return SpecificationUtils.likeMultiple(fields, terms);
  }
}
