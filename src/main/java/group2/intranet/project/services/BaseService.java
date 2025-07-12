package group2.intranet.project.services;

import org.springframework.data.jpa.domain.Specification;

import java.awt.print.Pageable;
import java.util.List;

public interface BaseService<T> {

    List<T> getAll();

    List<T> getAll(Specification<T> spec, Pageable pageable);

    T getById(Integer id);

    T create(T dto);

    T update(Integer id, T dto);

    void deleteById(Integer id);

}
