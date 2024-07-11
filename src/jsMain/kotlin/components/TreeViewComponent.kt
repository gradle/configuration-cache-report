package components

import elmish.View
import elmish.div
import elmish.ol
import elmish.span
import elmish.tree.Tree
import elmish.tree.TreeView
import elmish.tree.viewSubTrees

class TreeViewComponent<M, I>(
    private val viewNode: (Tree<M>) -> View<I>,
    treeIntent: (Tree.Focus<M>) -> I
) {

    private val treeButtonComponent = TreeButtonComponent(treeIntent)

    fun view(model: TreeView.Model<M>): View<I> {
        return viewTree(model)
    }

    fun step(intent: TreeView.Intent<M>, model: TreeView.Model<M>): TreeView.Model<M> = when (intent) {
        is TreeView.Intent.Toggle -> model.copy(
            tree = intent.focus.update {
                copy(state = state.toggle())
            }
        )
    }

    fun step(
        intent: TreeView.Intent<M>,
        model: TreeView.Model<M>,
        update: (M) -> M
    ): TreeView.Model<M> = when (intent) {
        is TreeView.Intent.Toggle -> model.copy(
            tree = intent.focus.update {
                copy(label = update(label))
            }
        )
    }

    private
    fun viewTree(model: TreeView.Model<M>): View<I> =
        viewSubTrees(model.tree.focus().children)

    private
    fun viewSubTrees(subTrees: Sequence<Tree.Focus<M>>): View<I> = div(
        ol(
            viewSubTrees(subTrees) { focus ->
                treeLabel(focus)
            }
        )
    )

    private
    fun treeLabel(focus: Tree.Focus<M>): View<I> = span(
        treeButtonComponent.view(focus),
        viewNode(focus.tree),
    )
}
