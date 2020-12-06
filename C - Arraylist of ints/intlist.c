#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <stdarg.h>
#include "intlist.h"

// internal utils

static void resize(intlist* list, int target_size)
{
    list->elements = realloc(list->elements, target_size * sizeof(int));
    list->capacity = target_size;
}

static void expand(intlist* list)
{
    int target_size = list->length < 20 ? list->length * 2 : list->length * 4/3;
    resize(list, target_size);
}

static void shrink(intlist* list)
{
    int target_size = list->capacity < 20 ? list->capacity * 3/5 : list->capacity * 4/5;
    if (target_size < list->length)
        return;
    resize(list, target_size);
}

// constructors

intlist list_new(void)
{
    return list_new_with_size(5);
}

intlist list_new_with_size(int initial_capacity)
{
    intlist list;
    list.length = 0;
    list.capacity = initial_capacity;
    list.elements = malloc(initial_capacity * sizeof(int));
    return list;
}

intlist list_new_from_array(int* array, int length)
{
    intlist list;
    list.length = length;
    list.capacity = length;
    list.elements = array;
    return list;
}

// destructor

void list_destruct(intlist* list)
{
    free(list->elements);
}

// public methods

void list_add(intlist* list, int element)
{
    if (list->length == list->capacity)
        expand(list);
    list->elements[list->length++] = element;
}

void list_add_all(intlist* list, int argc, ...)
{
    va_list valist;
    va_start(valist, argc);
    int final_size = list->length + argc;
    if (list->capacity < final_size)
        resize(list, final_size);
    for (int i=0; i<argc; i++)
        list->elements[list->length++] = va_arg(valist, int);
    va_end(valist);
}

void list_add_list(intlist* list, intlist* elements_to_add)
{
    int final_size = list->length + elements_to_add->length;
    if (list->capacity < final_size)
        resize(list, final_size);
    for (int i=0; i<elements_to_add->length; i++)
        list->elements[list->length++] = elements_to_add->elements[i];
}

// 0 success, 1 fail
int list_remove(intlist* list, int index)
{
    if (index < 0 || index >= list->length)
        return 1;

    for (int i=index+1; i<list->length; i++)
        list->elements[i-1] = list->elements[i];
    list->length--;
    if (list->length <= list->capacity / 2)
        shrink(list);
    return 0;
}

int list_index_of(intlist* list, int element)
{
    for (int i=0; i<list->length; i++)
        if (list->elements[i] == element)
            return i;
    return -1;
}

bool list_contains(intlist *list, int element)
{
    return list_index_of(list, element) != -1;
}

void list_for_each(intlist* list, void (*operation)(int element, int index))
{
    for (int i=0; i<list->length; i++)
        operation(list->elements[i], i);
}

intlist list_filter(intlist* list, bool (*operation)(int element, int index))
{
    intlist filtered = list_new_with_size(list->length / 2);
    for (int i=0; i<list->length; i++)
        if (operation(list->elements[i], i))
            list_add(&filtered, list->elements[i]);
    return filtered;
}

intlist list_map(intlist *list, int (*operation)(int element, int index))
{
    intlist mapped = list_new_with_size(list->length);
    for (int i=0; i<list->length; i++)
        list_add(&mapped, operation(list->elements[i], i));
    return mapped;
}

int list_reduce(intlist *list, int (*operation)(int prev, int cur, int index), int initial)
{
    int reduced = initial;
    for (int i=0; i<list->length; i++)
        reduced = operation(reduced, list->elements[i], i);
    return reduced;
}

char* list_to_string(intlist *list)
{
    int length = 1, max_length = 3 + list->length * 2;
    char* f_string = malloc(max_length);
    f_string[0] = '[';
    f_string[1] = '\0';
    for (int i=0; i<list->length; i++)
    {
        char* f_next = i == list->length - 1 ? "%d" : "%d, ";
        int next = snprintf(NULL, 0, f_next, list->elements[i]);
        length += next;
        if (length + 1 >= max_length)
        {
            max_length *= 3/2;
            realloc(f_string, max_length);
        }
        char* element = malloc(next + 1);
        snprintf(element, next + 1, f_next, list->elements[i]);
        strcat(f_string, element);
        free(element);
    }
    f_string[length] = ']';
    f_string[length+1] = '\0';
    return f_string;
}