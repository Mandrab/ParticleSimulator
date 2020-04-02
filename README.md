# First version results

|           | 1 Th    | 2 Th    | 4 Th   | 13 Th  |
|-----------|---------|---------|--------|--------|
| 5000-5000 | 151k    | 265'106 | 68'786 | 15'029 |
| 1000-1000 | 1'919   | 2'165   | 700    | 310    |
| 100-500   | 36      | 130     | 90     | 82     |

Maybe try:<br/>
- exec collision calc without wait all the thread (when a thread come, analize it's balls)<br/>
- grid map check (is cheat?)<br/>
- start from another index during collision calc (may help with less processors)<br/>

|           |         | without conflict array      | with conflict array         |
|           | 1 Th    | 13 Th + sync | 13 Th + lock | 13 Th + sync | 13 Th + lock | 
|-----------|---------|--------------|--------------|--------------|--------------|
| 5000-5000 | 170k    | 95k          | 110k         | 90k          | 75k          |
