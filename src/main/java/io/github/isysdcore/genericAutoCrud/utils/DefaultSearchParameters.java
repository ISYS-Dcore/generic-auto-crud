/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.isysdcore.genericAutoCrud.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 *
 * @author domingos.fernando
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultSearchParameters {

    private int page = 0;
    private int size = 10;
    private int sort = 1;
    private String query = "id==*";

    /**
     *
     * @param actualPage
     * @param actualSize
     * @param actualSort
     * @return
     */
    public static Pageable preparePages(int actualPage, int actualSize, int actualSort) {

        return actualSort > 0 ? PageRequest.of(actualPage, actualSize, Sort.by("cratedAt").ascending())
                : PageRequest.of(actualPage, actualSize, Sort.by("cratedAt").descending());
    }

}
