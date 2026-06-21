plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.2-fabric+noremap"

stonecutter {
    parameters {
        constants.match(
            node.metadata.project.substringAfterLast('-').substringBeforeLast('+'),
            "fabric", "neoforge"
        )
    }
}
