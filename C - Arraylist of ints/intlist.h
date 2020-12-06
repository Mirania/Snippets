#include <stdbool.h>
#include <stdarg.h>

#ifndef C_intlist_H
#define C_intlist_H

// An int array that automatically resizes itself when necessary. Supports many utility operations.
typedef struct intlist {
    // The array backing the list. A user should only access this array to read its values. To modify its contents, please use the utility functions.
    int* elements;
    // The number of elements in the list.
    int length;
    // The current maximum capacity of this list.
    int capacity;
} intlist;

// Create a new empty list.
intlist list_new(void);
// Create a new empty list with a user-defined initial capacity.
intlist list_new_with_size(int initial_capacity);
// Create a list from an int array. The list will be backed by the original array.
intlist list_new_from_array(int *array, int length);
// List destructor. This should be called at the end of a list's lifecycle whether it was instantiated as a stack or heap object.
void list_destruct(intlist* list);

// Add an element to a list.
void list_add(intlist* list, int element);
// Add an argc amount of elements to a list, e.g. list_add_all(&list, 3, 10, 20, 30).
void list_add_all(intlist *list, int argc, ...);
// Add all elements from the second list to the first list.
void list_add_list(intlist *list, intlist *elements_to_add);
// Remove an element from a list. Returns 0 on success and 1 on failure.
int list_remove(intlist *list, int index);
// Find an element in a list. Returns -1 if the element could be not found.
int list_index_of(intlist *list, int element);
// Check if a list contains an element.
bool list_contains(intlist *list, int element);
// Perform an operation for each element in a list.
void list_for_each(intlist *list, void (*operation)(int element, int index));
// Create a list that only contains the elements for which the operation returned true.
intlist list_filter(intlist* list, bool (*operation)(int element, int index));
// Create a list that contains the results of the supplied operation for each element.
intlist list_map(intlist* list, int (*operation)(int element, int index));
// Perform a reducing operation that returns a single int value.
int list_reduce(intlist* list, int (*operation)(int prev, int cur, int index), int initial);
// Get a readable and formatted representation of the contents of the list, e.g. [5, 103, 4904, 439].
char* list_to_string(intlist* list);

#endif