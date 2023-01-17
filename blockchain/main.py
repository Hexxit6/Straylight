#!/usr/bin/env python3

from Blockchain import *
from mpi4py import MPI
import multiprocessing
from datetime import datetime


def mine_block(prev_block, id, cores, rank, size, return_dict, run):
    """
    Mine a new block based on the previous blocks data.
    This is done by increasing the nonce.
    When the new block is mined, signal all processes to stop and return that block.
    Function could be 'improved' by adding data to parameters
    (current data is just for demonstrational purposes).
    """

    nonce = rank*cores+id
    timestamp = datetime.utcnow()
    index = prev_block.index + 1
    prev_hash = prev_block.hash
    diff = prev_block.diff
    data = "Straylight" + str(index)

    while run.is_set():
        while not comm.Iprobe():
            block = Block(index, timestamp, data, prev_hash, diff, nonce)
            # print(f"id: {rank*cores+id}, hash: {block.hash}, nonce: {nonce}") # DEBUGGING
            count = 0
            for zero in block.hash:
                if zero != '0': break
                count += 1
            if count == diff:
                run.clear()
                return_dict['block'] = block
                print(f"Block mined at: {rank*cores+id}") # DEBUGGING
                break
            nonce += cores*size
        if comm.Iprobe():
            break


# mpiexec -n 2 python main.py
if __name__ == "__main__":
    comm = MPI.COMM_WORLD
    rank = comm.Get_rank()
    size = comm.Get_size()

    blockchain = Blockchain()

    processes = []
    manager = multiprocessing.Manager()
    return_code = manager.dict()
    cores = 4

    run = manager.Event()
    run.set()

    while True:
        for i in range(cores):
            process = multiprocessing.Process(target=mine_block, args=(blockchain.chain[-1], i, cores, rank, size, return_code, run))
            processes.append(process)
            process.start()

        for process in processes:
            process.join()

        processes = []

        print(f"Found block:\n{return_code['block']}") # DEBUGGING
        return_code['block'] = comm.bcast(return_code['block'])
        blockchain.add_block(return_code['block'])
        run.set()


