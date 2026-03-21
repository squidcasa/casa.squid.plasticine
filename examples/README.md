# Plasticine Examples

This directory contains examples demonstrating the features and capabilities of the Plasticine UI library.

## Organization

The examples are organized into three categories:

1. **Component-Specific Examples** (`components/`) - Examples focusing on individual UI components
2. **Cross-Cutting Feature Examples** (`features/`) - Examples demonstrating library features that span multiple components
3. **Demo Applications** (`demos/`) - Complete example applications showcasing realistic use cases

## Running Examples

To run any example, use the Clojure REPL or command line. For example:

```bash
clojure -M examples/components/rotary/basic_rotary.clj
```

Or load the namespace in your REPL:

```clojure
(require 'examples.components.rotary.basic-rotary)
```

## Component Examples

### Rotary
- `basic_rotary.clj` - Simplest possible rotary knob with default styling
- `styled_rotary.clj` - Customizing colors, size, and appearance
- `rotary_with_model.clj` - Connecting a rotary knob to an atom for data binding
- `rotary_with_callback.clj` - Using the on-change callback to respond to value changes
- `multiple_rotaries.clj` - Showing several knobs with different configurations
- `custom_drawing_rotary.clj` - Advanced customization of the drawing function

### Sliders
- `basic_hslider.clj` - Simple horizontal slider with default styling
- `basic_vslider.clj` - Simple vertical slider with default styling
- `styled_sliders.clj` - Customizing colors, sizes, and appearance
- `slider_with_model.clj` - Connecting sliders to atoms for data binding
- `slider_with_callback.clj` - Using the on-change callback to respond to value changes
- `hv_comparison.clj` - Side-by-side comparison of both slider types

### Grid
- `basic_grid.clj` - Simple grid with default configuration
- `weighted_grid.clj` - Using weight specifications for flexible layouts
- `auto_grid.clj` - Using auto-sizing for content-aware layouts
- `constrained_grid.clj` - Applying constraints to grid dimensions
- `nested_grid.clj` - Grids containing other grids for complex layouts
- `mixed_grid.clj` - Grid containing different types of components

### Container
- `stack_layout.clj` - Vertical stacking of components with gaps
- `row_layout.clj` - Horizontal arrangement of components
- `column_layout.clj` - Vertical arrangement of components
- `nested_containers.clj` - Combining different container types

### Select List
- `basic_select_list.clj` - Simple list with keyboard navigation
- `select_list_callback.clj` - Handling selection changes
- `styled_select_list.clj` - Customizing the appearance

## Feature Examples

- `data_binding.clj` - Demonstrating one-way and two-way data binding between components
- `event_handling.clj` - Various approaches to handling user interactions
- `custom_styling.clj` - Advanced styling techniques using the draw system
- `component_composition.clj` - Building complex UIs by combining simpler components
- `layout_management.clj` - Using different layout strategies together
- `state_management.clj` - Managing application state with atoms and watches

## Demo Applications

- `simple_mixer.clj` - Audio mixer-style interface with multiple rotary knobs controlling volume levels
- `parameter_control_panel.clj` - Dashboard with various controls for adjusting parameters
- `interactive_visualization.clj` - UI controls that affect a visual display
- `settings_panel.clj` - Application settings interface with different control types
- `music_synth.clj` - Simple synthesizer UI with knobs, sliders, and buttons
- `data_filter.clj` - Interface for filtering and manipulating data with multiple controls

Each example is designed to be run independently and demonstrates specific aspects of the Plasticine library.