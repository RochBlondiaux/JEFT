package me.rochblondiaux.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class Pagination<T> {

    /**
     * @param list
     * @param pageSize
     * @param page
     * @return
     */
    public List<T> paginateReverse(List<T> list, int pageSize, int page) {
        List<T> currentList = new ArrayList<>();
        if (page == 0)
            page = 1;
        int idStart = list.size() - 1 - ((page - 1) * pageSize);
        int idEnd = idStart - pageSize;
        if (idEnd < list.size() - pageSize && list.size() < pageSize * page)
            idEnd = -1;
        for (int a = idStart; a != idEnd; a--)
            currentList.add(list.get(a));
        return currentList;
    }

    /**
     * @param map
     * @param pageSize
     * @param page
     * @return
     */
    public List<T> paginateReverse(Map<?, T> map, int pageSize, int page) {
        List<T> currentList = new ArrayList<>();
        map.forEach((k, v) -> currentList.add(v));
        return paginateReverse(currentList, pageSize, page);
    }

    /**
     * @param map
     * @param pageSize
     * @param page
     * @return
     */
    public List<T> paginate(Map<?, T> map, int pageSize, int page) {
        List<T> currentList = new ArrayList<>();
        map.forEach((k, v) -> currentList.add(v));
        return paginate(currentList, pageSize, page);
    }

    /**
     * @param list
     * @param pageSize
     * @param page
     * @return
     */
    public List<T> paginate(List<T> list, int pageSize, int page) {
        List<T> currentList = new ArrayList<>();
        if (page == 0)
            page = 1;
        int idStart = ((page - 1)) * pageSize;
        int idEnd = idStart + pageSize;
        if (idEnd > list.size())
            idEnd = list.size();
        for (int a = idStart; a != idEnd; a++)
            currentList.add(list.get(a));
        return currentList;
    }

}