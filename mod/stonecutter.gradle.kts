plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active file("stonecutter.active")

stonecutter parameters {
    constants.match(
        node.metadata.project.substringAfterLast('-'),
        "fabric", "neoforge"
    )
}
