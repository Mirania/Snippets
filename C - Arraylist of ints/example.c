#include <stdio.h>
#include <stdbool.h>
#include "intlist.h"

void foreach(int x, int i) {
    printf("index %d -> %d\n", i, x);
}

bool filter(int x, int i) {
    return x < 25;
}

int reduce(int x, int y, int i) {
    return x + y;
}

int main()
{
    intlist list = list_new();
    list_add(&list, 176);
    list_add_all(&list, 2, 22, 34435);
    printf("list: %s\n", list_to_string(&list));
    
    intlist list2 = list_new();
    list_add(&list2, 7);
    list_add_list(&list, &list2);
    printf("joined list: %s\n", list_to_string(&list));

    intlist filtered = list_filter(&list, &filter);
    printf("filtered: %s\n", list_to_string(&filtered));
    list_destruct(&filtered);
    
    printf("sum: %d\n", list_reduce(&list, &reduce, 0));
    list_for_each(&list, &foreach);
    list_remove(&list, 0);
    list_remove(&list, 2);
    printf("list: %s with length %d\n", list_to_string(&list), list.length);
    list_destruct(&list);
}