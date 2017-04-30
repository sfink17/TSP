# Module TSP

This module includes several heuristics for the Travelling Salesman Problem.
                                                                                                   Currently included heuristics are "Nearest Neighbor," "Nearest Insertion," and "Greedy."
                                                                                                   Heuristics to be added are a variation of Helsgaun's Lin Kernighan implementation, using
                                                                                                   4-opt moves as a basis, and a simulated quantum annealing heuristic.

                                                                                                   The module includes a basic GUI, but in lieu of this, it may be driven from the command line;
                                                                                                   simply remove the relevant comments in TSP.kt. From the command line, the module takes two
                                                                                                   arguments: the pathname to the file, and one of three heuristic abbreviations (nn, ni, and greedy).