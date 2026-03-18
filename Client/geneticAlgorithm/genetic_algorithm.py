import sqlite3
import random
import copy
import math
import json
import uuid
import os

# Genetic Algorithm Parameters

path = os.path.realpath(__file__)
path = path[:path.rfind("/") + 1]
if (len(path) == 0):
	path = os.path.realpath(__file__)
	path = path[:path.rfind("\\") + 1]

POPULATION_SIZE = 100
CROSSOVER_RATE = 0.8
ELITISM_RATE = 0.1
MUTATION_RATE = 0.2
TOURNEY_SIZE = 3
MAX_STAGNANT_GENERATIONS = 50
MAX_GENERATIONS = 100

# Problem-Specific Variables
STARTING_WAREHOUSE_X = 0
STARTING_WAREHOUSE_Y = 0

DATABASE_FILENAME = path + 'delivery_stops.db'


# Database Operations

def setup_database():
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS delivery_stops (
        StopsSetID INTEGER,
        X INTEGER,
        Y INTEGER
    )''')
    conn.commit()
    conn.close()

def populate_database():
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()
    # Check if data already exists
    cursor.execute('SELECT COUNT(*) FROM delivery_stops')
    if cursor.fetchone()[0] == 0:
        stops = [(0, random.randint(-15, 15), random.randint(-15, 15)) for _ in range(100)]
        cursor.executemany('INSERT INTO delivery_stops (StopsSetID, X, Y) VALUES (?, ?, ?)', stops)
        conn.commit()
    conn.close()

def load_stops_from_database():
    conn = sqlite3.connect(DATABASE_FILENAME)
    cursor = conn.cursor()
    cursor.execute("SELECT X, Y FROM delivery_stops")
    stops = [{'X': x, 'Y': y} for (x, y) in cursor.fetchall()]
    conn.close()
    return stops

# Genetic Algorithm Implementation

class CandidateSolution:
    def __init__(self, stops):
        self.path = random.sample(stops, len(stops))
        self.fitness_score = self.calculate_fitness()

    def calculate_fitness(self):
        total_distance = 0
        for i in range(len(self.path)-1):
            stop1, stop2 = self.path[i], self.path[i+1]
            distance = math.sqrt((stop2['X'] - stop1['X'])**2 + (stop2['Y'] - stop1['Y'])**2)
            total_distance += distance
        return total_distance

def select_parents(population):
    selected = random.sample(population, TOURNEY_SIZE)
    selected.sort(key=lambda x: x.fitness_score)
    return selected[0], selected[1]

def crossover(parent1, parent2):
    if random.random() > CROSSOVER_RATE:
        return copy.deepcopy(parent1), copy.deepcopy(parent2)
    child1, child2 = copy.deepcopy(parent1), copy.deepcopy(parent2)
    index1, index2 = sorted(random.sample(range(len(parent1.path)), 2))
    middle1 = parent1.path[index1:index2]
    middle2 = parent2.path[index1:index2]
    remaining1 = [stop for stop in parent2.path if stop not in middle1]
    remaining2 = [stop for stop in parent1.path if stop not in middle2]
    child1.path = remaining1[:index1] + middle1 + remaining1[index1:]
    child2.path = remaining2[:index1] + middle2 + remaining2[index1:]
    child1.fitness_score = child1.calculate_fitness()
    child2.fitness_score = child2.calculate_fitness()
    return child1, child2

def mutate(solution):
    if random.random() < MUTATION_RATE:
        i, j = random.sample(range(len(solution.path)), 2)
        solution.path[i], solution.path[j] = solution.path[j], solution.path[i]
        solution.fitness_score = solution.calculate_fitness()

def run_genetic_algorithm(stops):
    population = [CandidateSolution(stops) for _ in range(POPULATION_SIZE)]
    with open(path + "best_fitness_file.txt", "w") as file:
        for generation in range(MAX_GENERATIONS):
            population.sort(key=lambda x: x.fitness_score)
            next_gen = population[:int(len(population) * ELITISM_RATE)]
            while len(next_gen) < POPULATION_SIZE:
                parent1, parent2 = select_parents(population)
                child1, child2 = crossover(parent1, parent2)
                mutate(child1)
                mutate(child2)
                next_gen.extend([child1, child2])
            population = next_gen
            file.write(f"Generation {generation+1}: Best Fitness = {population[0].fitness_score}\n")
    return population[0].path


# Genetic Algorithm Function

def genetic_algorithm():
    setup_database()
    populate_database()
    stops = load_stops_from_database()
    return run_genetic_algorithm(stops)
