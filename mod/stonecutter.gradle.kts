plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11-fabric"

stonecutter {
    parameters {
        constants.match(
            node.metadata.project.substringAfterLast('-'),
            "fabric", "neoforge"
        )
    }
}
