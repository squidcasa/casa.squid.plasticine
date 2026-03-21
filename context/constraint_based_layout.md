The current layouting is a bit of a mess, we are going to fix it.

Currently we have min/max/preferred sizing, instead we are going to replace all
of that with a single method: `-layout-size`. This takes the current object
(component/container), and a constraints object, `[min-width max-width min-height max-height]` 

The child must pick a size that fits within those constraints and report it
back, so the return value is `[width height]`

In addition, children can have a `:flex-width` and/or `:flex-height` attribute,
both defaulting to 0. If not set or 0, there is no flex, i.e. fixed. This way,
when a container has "leftover" size, it gets distributed proportionally to
children that have flex, proportional to their flex factor in the given
dimension. `:flex x` is a shorthand for `:flex-width x, :flex-height x`, i.e.
add component accessors that check the dimension-specific version, and fall back
to `:flex`, then fall back to 0.

The 3-Step Layout Algorithm

For a container like a Column or Stack, the logic should look like this:

- Layout Fixed Children: Ask children with no "flex" factor how big they want to be. Give them maxWidth of the container and unconstrained height (Long/MAX_VALUE).
- Calculate Remaining Space: Subtract the height of all fixed children from the total available height.
- Distribute to Flex Children: Divide the remaining space among "grow" children based on their flex factor.

## Intrinsic sizing

Also add intrinsic-size methods

The absolute minimum width this child needs to not break. 
Example: For a sentence, the width of the longest word.
`-min-intrinsic-height [this width]`

The "ideal" width. 
Example: For a sentence, the width of the text in a single line. 
`-max-intrinsic-height [this width]`

The absolute minimum height this child needs. 
`-min-intrinsic-width [this height]`

The ideal height. 
`-max-intrinsic-width [this height]`

Note: Notice that each method takes the other dimension as an argument. This is
the "Constraint Dependency." If I tell a text block I'm forcing it to be 100px
wide, its max-intrinsic-height will increase because the text will wrap.

Here is the lifecycle of a single component being laid out by a parent:

- The Scout Phase: The parent calls max-intrinsic-width(Infinity). The child says "I'd love to be 500px."
- The Decision: The parent looks at its own constraints. It only has 300px available.
- The Order: The parent calls layout-size([minWidth= 0, maxWidth= 300, ...]).
- Adaptation: The child sees the maxWidth: 300. It wraps its text, realizes it now needs to be 60px tall instead of 20px, and returns size [300, 60].
