# Airport Simulation Using Queues (One Runway)

## Problem Statement
Simulate a small airport that has only **one runway**.  
There are:
- A **landing queue** (planes waiting to land)
- A **takeoff queue** (planes waiting to take off)

Only **one plane can use the runway at a time** (either one landing or one takeoff).

Rules:
- Landing and takeoff each take a fixed amount of time (landing time may differ from takeoff time).
- Planes arrive randomly into the landing queue using a given average arrival interval.
- Planes arrive randomly into the takeoff queue using a given average departure interval.
- **Landing has priority**: if at least one plane is waiting to land, no takeoff can happen.
- Planes waiting to land can crash if they wait too long (run out of fuel).
- A crashed plane should not be included in waiting-time averages.
- At the end of the simulation, planes still waiting can be ignored except those that would have crashed.

## Inputs
1. Minutes needed for one landing  
2. Minutes needed for one takeoff  
3. Average time between landing arrivals  
4. Average time between takeoff arrivals  
5. Maximum time a plane can stay in landing queue before crashing  
6. Total simulation minutes  

## Outputs
1. Number of planes that took off  
2. Number of planes that landed  
3. Number of planes that crashed  
4. Average waiting time in takeoff queue  
5. Average waiting time in landing queue  

## Solution Logic (Approach)
This simulation is done **minute-by-minute** using an integer clock `t`.

### Key Data Structures
- `Queue<Integer> landingQueue`: stores the minute a plane entered the landing queue
- `Queue<Integer> takeoffQueue`: stores the minute a plane entered the takeoff queue  
Queues are used because planes must be processed **FIFO** (first-in-first-out).

### Random Arrivals
The problem provides an **average interval** between planes.
We convert it to a probability per minute:
- `pLanding = 1 / avgLandingInterval`
- `pTakeoff = 1 / avgTakeoffInterval`

Each minute:
- if `rand < pLanding`, a landing plane arrives (store `t` in landing queue)
- if `rand < pTakeoff`, a takeoff plane arrives (store `t` in takeoff queue)

### Runway Busy Logic
We model the runway using a countdown timer:
- `runwayRemaining` = minutes left until runway becomes free
- `runwayMode` = 1 for landing, 2 for takeoff, 0 for idle

Each minute:
1. If runway is busy → decrement `runwayRemaining`
2. If it reaches 0 → that operation finished, increment landed/takeoff count

### Landing Priority
If the runway is free:
- If landing queue is non-empty → start landing
- Else if takeoff queue is non-empty → start takeoff

### Crash Handling
Planes crash if they have waited too long in the landing queue.
Crash is “discovered” when we are about to process the landing queue:
- While the first landing plane waited more than `MAX_AIRTIME`, remove it and count a crash
- Crashed planes are discarded and not included in waiting time

### Waiting Time Calculation
When a plane starts landing or takeoff:
- `wait = currentTime - timeEnteredQueue`
- Add to the total waiting time accumulator
Average wait = total wait / number of completed operations

### End-of-Simulation Crash Check
After the main loop ends, remaining landing queue is checked:
- Count planes that would have exceeded `MAX_AIRTIME` by the end time
- Other planes still waiting are ignored

## How to Run
Compile and run the Java file, then enter the input values when prompted.
