# Results

Maybe try:<br/>
- exec collision calc without wait all the thread (when a thread come, analize it's balls)<br/>
- grid map check (is cheat?)<br/>
- start from another index during collision calc (may help with less processors)<br/>

## Conflict array + Synch vs Lock comparison


| bodies-step | Prof. seq version (1Th) | sync (13Th) | lock (13Th) | sync + C.A. (13Th) | lock + C.A. (13Th) |
|---          |---                      |---          |---          |---                 |---                 |
| 5000-5000   | 170 s                   | 95 s        | 110 s       | 90 s               | 75 s               |


## Bodies subdivision comparision

The new subdivision logic aim to distribute calcs more equally over simulators.

![new subdivision logic](res/bodies_subdivisions.png)


|          | index range (basic div) | custom div (try-catch)      | custom div (no try-catch<sup id="a1">[1](#f1)</sup>)	|
|---       |---                           |---            |---         |
| 1th run  | 29, 34, 32,                  | 25, 27, 28,	  | 30, 30, 31,|
|          | 33, 34 (s)                   | 29, 34 (s)	  | 29, 32 (s) |
| 2dn run  | 33, 33k, 34,                 | 27, 26, 26,	  | 31, 33, 32,|
|          | 35, 34k (s)                  | 29, 28 (s)	  | 31, 32 (s) |
| 3rd run  | 30, 31, 37,                  | 29, 28, 29,	  | 29, 31, 30,|
|          | 35, 31 (s)                   | 27, 30, 27 (s)| 35, 31 (s) |
| **min:** | **29 s**                     | **25 s**      | **29 s**   |
| **max:** | **37 s**                     | **34 s**      | **35 s**   |
| **avg:** | **33 s**                     | **28 s**      | **31 s**   |
    
<b id="f1">[1]</b> A check is necessary. This try was made to point out if the try-catch block insert a big overhead (response: NO). [↩](#a1)
