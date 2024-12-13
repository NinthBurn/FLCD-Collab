# Teamwork Project - CALINESCU Florin, BUDASU Razvan-Fabian

## Parser Documentation

### `adt` package
- **ParseTreeNode** - class that represents a node within the parser tree. Contains a string for the symbol it represents, a link to its parent (as a **ParseTreeNode** object) and a list of links (as **ParseTreeNode** objects) to its children. The order of the children is from left to right, from insertion. When we translate a production to it, the left hand side is the symbol, and the right hand side contains the children, as new nodes, with the current node as a parent.
	- `isLeaf()` returns true if the node does not have any children.
	- `toStringTree()` is adjusted to graphically represent the tree, with a `maxDepth` variable to avoid infinite recursion 
	
- **ParseTreeTable** - class that contains the output band of the LR(0) parser, as a table with references to children and parents. Contains a **ParseTreeNode** object for the root of the tree, as well as methods to retrieve the root, leaves and nodes of the tree.
	- `getLeaves()` returns the leaves of the tree, via a DFS recursive algorithm, and `getNodes()` returns all nodes of the tree, by the same method.
	- `toString()` utilizes an auxiliary class for displaying the results within a table

### `parser` package
- **AnalysisItem** - class that holds a part of the state for the LR(0) canonical collection. Contains members for the left hand side of the production, the right hand side, and the position of the dot
	- `getSymbolAtCursor()` returns the symbol preceding the dot in the right hand side
	- `moveCursorRight()` moves the dot and returns the new AnalysisItem with the correct dot position
	- `cursorAtEnd()` checks for the position of the dot, and returns true if it's at the end of the right hand side
	- `equals(Object obj)` checks for object equality based on left hand side and right hand side equality, and dot position

- **Production** - class similar to **AnalysisItem** in terms of meaning, but used for output generation. Contains members for the left hand side of the production, the right hand side and the index of the production.
	- comparison between this and **AnalysisItem** is done by comparing the left and right hand sides

- **State** - class that holds a single unit within a canonical collection. Contains a list of **AnalysisItem** objects.
	- comparison is done at the level of the **AnalysisItem** objects within the list

- **CanonicalCollection** - class that holds the basis of operations for the LR(0) parser. Contains a list of **State** objects, a dictionary that holds pairs of integers and strings as keys, and integers as values (i.e. from state 1, with symbol A, we'd go to state 2), and an **AnalysisItem** object for the `accept` state, for ease of use when performing the parsing.
	- `connectStateToAnother(Integer stateFrom, String symbol, Integer stateTo)` will create a new pair from the `stateFrom` and `symbol` variables, and map `stateTo` to it within the residing dictionary.

- **table/ParserTableAction** - class that holds an action for a row within the LR(0) parsing table. Contains an enum with the `ACCEPT`, `SHIFT` and `REDUCE` states, and a production index for the `REDUCE` operation.

- **table/ParserTableRow** - class that holds a row from the table that we'd generate from the **CanonicalCollection** object in the LR(0) parsing process. Contains an integer for the state index, a **ParserTableAction** object, and a dictionary with strings as keys and integers as values (i.e. on this row, row 1, symbol A would move us to row 2 under a `SHIFT` action)
	- `setAction(ParserTableAction action)` checks for action conflicts. (e.g. the same row would do `SHIFT` and `ACCEPT`)

- **Grammar** - class that contains the main ruleset on which we'd base the parsing. Contains variables for separators and specific symbols, lists for all of the terminals and non-terminals, a dictionary with strings as keys and lists of strings as values (i.e. this is the basic representation of a production, with the key being the left hand side and the value being all of the symbols on the right hand side), and the starting symbol.
	- `extractFromFile(String filename)` parses a file that follows the guidelines, and generate a **Grammar** object from it
	- `isContextFree()` checks if we have a CFG by seeing if the left hand sides of all productions contain just one non-terminal symbol.
	- `getEnrichedGrammar()` is what prepares the grammar for LR(0) parsing, as it copies the current grammar, adds the enrichment symbol to the non-terminal list, and a new production, containing the enrichment symbol on the left hand side, and all starting symbols on the right.

- **LR0Parser** - the core of the assignment. Contains the grammar we have to parse, a list of **ParserTableRow** objects that compose a table, and a list of **Production** objects, that contains all of the productions of our given grammar.
	- `closure(State initialState)` is one of the main operations in generating the canonical collection. It takes in the initial state, and adds new **AnalysisItem** objects that fit the criteria to it (i.e. ones that have on the left hand side the symbol that's after the dot in the current state)
	
	- `goTo(State initialState, String symbol)` is the other one of the main operations in generating the canonical collection. Within the initialState, if we find a dot preceding the given symbol, we move the dot to the right, and then add the `closure` of the state after the move operation into our list. 
	
	- `getCanonicalCollection()` utilizes the previously mentioned functions to generate the canonical collection from the given grammar. We start from the base state, the one that contains the enrichment symbol, and we add its `closure`. Afterwards, we set that as the `ACCEPT` state in the table. Finally, we go through the states of the canonical collection, and perform the goTo operation on them. Also, we utilize the `connectStateToAnother` function to prepare the table while generating the collection.
	
	- `buildParserTable(CanonicalCollection canonicalCollection)` takes in part one of the parser, and generates rows for the table from all of its states. It utilizes `determineParserTableRowAction` and `determineGoToColumnValues` that.
	
	- `determineParserTableRowAction(ParserTableRow currentRow, AnalysisItem analysisItem, CanonicalCollection canonicalCollection)` is the function that dictates what operation will the row perform. `ACCEPT` is predefined in the canonical collection, `REDUCE` happens if the current state has the cursor at the end, and the production index is retrieved from the list object. `SHIFT` happens if the other two don't, and the mapping is already done by now at canonical collection generation.
	
	- `determineGoToColumnValues(ParserTableRow currentRow, Integer stateIndex, CanonicalCollection canonicalCollection)` is what translates the mapping done in the canonical collection and writes it to the table.
	
	- `buildParseTree(Stack<Integer> outputBand)` takes the output band from a successful parsing (i.e. a list of production indexes), and creates the tree from said production. This is a top-down approach, as we take the first production, make its left hand side the root, and then go down on the rightmost non-terminal leaves and add the rest of the productions until the output band becomes empty 

	- `parseMyBitchUp(List<String> sequence)` is the core of the LR(0) parsing operation. Contains 3 stacks, one for the input (the given sequence), one used for all intermediate operations (i.e. `ACCEPT`, `SHIFT` and `REDUCE`) (this is the work stack), and one for the output band, containing the indexes for the necessary productions.
	  While the work stack is not empty, we peek at its top, and if it matches the given criteria (i.e. to match this regex: `s[0-9]+`, resulting in tokens like `s0` or `s5`), we get the corresponding parser table row, and perform its operation.
	  If we get to an `ACCEPT`, the parsing is done. A `SHIFT` would take a member from the input stack and move it to the work stack, followed by the state we'd go to from the symbol. Lastly, a `REDUCE X` would push `X` to the output band, pop the symbols (and their corresponding states) found within the right hand side of the production of index `X` from the work stack, then push the left hand side of the production of index `X`, alongside its corresponding state.
	  Finally, it generates the tree by using the functions `writeParseTreeToString` and `buildParseTree`.