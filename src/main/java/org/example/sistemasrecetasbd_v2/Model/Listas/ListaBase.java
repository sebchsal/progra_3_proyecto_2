package org.example.sistemarecetas.Model.Listas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class ListaBase<T> implements Iterable<T> {
    private final List<T> items = new ArrayList();
    private final Function<T, Integer> idGetter;

    public ListaBase(Function<T, Integer> idGetter) {
        this.idGetter = (Function)Objects.requireNonNull(idGetter);
    }

    public List<T> getItems() {
        return this.items;
    }

    public int size() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public Optional<T> buscarPorId(int id) {
        for(T t : this.items) {
            Integer cur = (Integer)this.idGetter.apply(t);
            if (cur != null && cur == id) {
                return Optional.of(t);
            }
        }

        return Optional.empty();
    }

    public void agregarOReemplazar(T t) {
        this.items.add(t);
    }

    public boolean eliminarPorId(int id) {
        return this.items.removeIf((t) -> {
            Integer cur = (Integer)this.idGetter.apply(t);
            return cur != null && cur == id;
        });
    }

    public void setAll(Collection<? extends T> col) {
        this.items.clear();
        if (col != null) {
            this.items.addAll(col);
        }

    }

    public Iterator<T> iterator() {
        return this.items.iterator();
    }
}