# OmegaT Color Theme Manager plugin

This is pretty straightforward color theme manager that lets you visualize how certain colors and fonts will look in OmegaT.

<img width="934" height="1006" alt="image" src="https://github.com/user-attachments/assets/92c1054e-9ffe-420e-a060-ef3335db8656" />


**Features**

- Live preview of color and font changes

- Select, copy, paste, and reset colors

- Support for foreground-only or background-only colors

- Two sample text inputs to preview font coverage for source and target languages

- Import and export color themes


## Building

To build the plugin, run:
```bash
./gradlew installDist
```

## Dependencies

OmegaT and other plugin dependencies are located in remote Maven repositories.
Make sure you have an internet connection to compile the project.

This plugin is built for OmegaT 6.0.0 and will **not** work with earlier versions.

## Where to find the built artifact?

After building, distribution files can be found in the folder `build/distributions/install` in your local repository copy.

You can also download prebiult binaries from the [Releases](https://github.com/kosivantsov/omegat-plugin-colorthememanager/releases) section.


## Installation

To install the plugin:

1. Extract the plugin JAR file from the ZIP distribution.

2. Place the plugin JAR in the plugins subfolder of your OmegaT user configuration folder (go to **Options** → **Access Configuration Folder** in OmegaT; create plugins if it doesn't exist).

3. OmegaT scans plugins recursively in that folder. If you have multiple plugins, you can organize each into its own subfolder for convenience, but it’s not required.


## License

This project is distributed under the GNU General Public License version 3 or later.


## Acknowledgements

A huge thank you to the OmegaT development team for creating and maintaining a CAT tool I love using and tweaking.

My gratitude also goes to [Hiroshi Miura](https://github.com/miurahr) and [Briac Pilpré](https://github.com/briacp) for crafting plenty of fantastic OmegaT plugins that serve as my personal inspiration—and my first go-to place for perfectly legal, shameless code stealing. I'm eternally thankful, dudes!

Thank you also to Dmitri Prihodko for implementing a similar thing in [similar thing in Pascal/Lazarus](https://sourceforge.net/projects/omegat-color-schemes-manager). I couldn't get his gizmo running these days, but now I don't even need to!

## Support/Coffee

If you want to show your appreciation:

<a href="https://www.buymeacoffee.com/verdakafo" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/default-green.png" alt="Coffee would be great" height="41" width="196"></a>
