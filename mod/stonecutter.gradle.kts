plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.20.1-fabric"

stonecutter {
    parameters {
        constants.match(
            node.metadata.project.substringAfterLast('-'),
            "fabric", "neoforge"
        )
    }
}
