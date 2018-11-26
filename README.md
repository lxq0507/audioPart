1st, Convert the wav to pcm,

2. What we get in the two dimension array is time-domain. Do a STFT to get frequency domain signal.

2.1  Use FFT to reduce time.O(n2) to O(nlong(n))

2.2  Since the common Sample Rate is 44,000Hz, if 4000Hz window size, the resolution of Frequency is 10Hz, Time resonlution is 0.1s. Add F resolution,reduce time resolution. So the time and frequency resolution is settled. About 5000Hz / 10Hz * fileTime / 0.1s

3. Get 2 peak values after FFT as landmarks per window. The number of landmarks is landmarkNumber * totalTime / windowSize. Since resolution is set, the number of landmarks should obey it. If it’s too small, it cannot describe the features with enough “detail”(too rough). But if too many, a file’s will have great overlap with others. After many test and experiment, choose 2 as landmarkNumber, 8000 as window size.

4. choose a target zone to get fingerprint. Only with landmarks(frequency) and time, resolution is not big enough. So add Difference of Frequency. Set a target zone like 2000Hz and 1s 

5.  set f and t as fingerprint. Every file has about 200 fingerprints. Create a Hash Table with fingerprints as key and list of files as value. Use 7 bits representing 7 files(1: has the fingerprint, 0: has not the fingerprint)
