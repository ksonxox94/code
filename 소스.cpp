#include <string>
#include <vector>
#include <iostream>
#include <algorithm>
#include <queue>

using namespace std;

bool compare(pair<int, int> a, pair<int, int> b) {
    return a.second < b.second;
}
int solution(vector<int> food_times, long long k)
{
    vector<pair<int, int>> foods;
    int n = food_times.size();
    for (int i = 0; i < n; ++i)
        foods.push_back(make_pair(food_times.at(i), i + 1));
    sort(foods.begin(), foods.end());

    int pretime = 0;
    for (vector<pair<int, int>>::iterator it = foods.begin(); it != foods.end();++it,--n)
    {
        long long spend=long long(it->first - pretime)* n;
        if (spend == 0) continue;
        if (spend <= k)
        {
            k = k - spend;
            pretime = it->first;
        }
        else
        {
            k = k % n;
            sort(it, foods.end(), compare);
            return (it + k)->second;
        }
    }
    return -1;

}

int solution2(vector<int> food_times, long long k) {
    long long summary = 0;
    for (int i = 0; i < food_times.size(); i++) {
        summary += food_times[i];
    }
    if (summary <= k) return -1;

    priority_queue<pair<int, int> > pq;
    for (int i = 0; i < food_times.size(); i++) {
        pq.push({ -food_times[i], i + 1 });
    }

    summary = 0;
    long long previous = 0; 
    long long length = food_times.size(); 

    while (summary + ((-pq.top().first - previous) * length) <= k) {
        int now = -pq.top().first;
        pq.pop();
        summary += (now - previous) * length;
        length -= 1; 
        previous = now; 
    }

    vector<pair<int, int> > result;
    while (!pq.empty()) {
        int food_time = -pq.top().first;
        int num = pq.top().second;
        pq.pop();
        result.push_back({ food_time, num });
    }
    sort(result.begin(), result.end(), compare); 
    return result[(k - summary) % length].second;
}

int main()
{
    int n;
    int x;
    long long k;
    vector<int> v;
    cin >> n;
    cin >> k;
    for (int i = 0; i < n; i++)
    {
        cin >> x;
        v.push_back(x);
    }
    cout<<solution(v, k);
}