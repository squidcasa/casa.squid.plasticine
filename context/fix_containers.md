The containers namespace currently has a major flaw, containers "grow" to fill
the available space, but they layout based on the size of their children. When
nesting containers, this leads to the first child container taking up all the
available space. We also only have one size calculation and layout approach, for
min, max, and pref size, whereas these methods are meant to be used in a
heuristic way to divide the space in a smart and proportional way.

Adjust container.clj to correctly handle min/max/pref, based on the min/max/pref
size of their children.

Size types:

- min - based on the min-size of the children, e.g. a stack's minimum width is
  the width of its widest child, its minimum height is the sum of the min height
  of all its children
- max - size of its bounds, regardless of child size, containers can always grow
  to whatever size is available.
- pref-size - start from pref-size of children. If the result is too large,
  proportionally reduce the size of the children, based on the difference
  between min and preferred, or if that is still too large, further reduce past
  the min-size until everything fits.
  
Containers

- stack: top to bottom, so width is widest element, height is sum of heights
- cols: left to right, so width is sum of widths of children, height is height of tallest child
