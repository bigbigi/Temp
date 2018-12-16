/*
This file is part of the project TraceroutePing, which is an Android library
implementing Traceroute with ping under GPL license v3.
Copyright (C) 2013  Olivier Goutay

TraceroutePing is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

TraceroutePing is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with TraceroutePing.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.reomote.carcontroller.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * This class contain everything needed to launch a traceroute using the ping
 * command
 *
 * @author Olivier Goutay
 */
public class TracerouteWithPing {

    private static final String TAG = "TracerouteWithPing";

    private static final String PING = "PING";

    private static final String FROM_PING = "From";

    private static final String PARENTHESE_OPEN_PING = "(";

    private static final String PARENTHESE_CLOSE_PING = ")";

    private static final String TIME_PING = "time=";

    private static final String RECEIVED = "received";

    private static final String RTT = "rtt";

    private static final String PERCENT = "%";

    private static final String EQUAL = "=";

    private int finishedTasks;


    private Context context;

    private static final int TIMEOUT = 15000;

    private Handler handlerTimeout;

    private static Runnable runnableTimeout;

    private OnTraceRouteListener mOnTraceRouteListener;

    public interface OnTraceRouteListener {
        public void onResult(int what, int loss, int delay);

        public void onTimeout(int what);

        public void onException(int what);
    }

    public TracerouteWithPing(Context context) {
        this.context = context;
    }

    /**
     * Launches the Traceroute
     *
     * @param url    The url to trace
     * @param maxTtl The max time to live to set (ping param)
     */
    public void executeTraceroute(String url, int what) {
        this.finishedTasks = 0;
        new ExecutePingAsyncTask(url, what).execute();
    }

    /**
     * Allows to timeout the ping if TIMEOUT exceeds. (-w and -W are not always
     * supported on Android)
     */
    private class TimeOutAsyncTask extends AsyncTask<Void, Void, Void> {

        private ExecutePingAsyncTask task;

        private int what;

        public TimeOutAsyncTask(ExecutePingAsyncTask task, int what) {
            this.task = task;
            this.what = what;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (handlerTimeout == null) {
                handlerTimeout = new Handler();
            }

            // stop old timeout
            if (runnableTimeout != null) {
                handlerTimeout.removeCallbacks(runnableTimeout);
            }
            // define timeout
            runnableTimeout = new Runnable() {
                @Override
                public void run() {
                    if (task != null) {
                        task.setCancelled(true);
                        task.cancel(true);
                        if (mOnTraceRouteListener != null && !task.isfinished) {
                            Log.e(TAG, what + " task.isFinished()"
                                    + finishedTasks);
                            mOnTraceRouteListener.onTimeout(what);
                        }
                    }
                }
            };
            // launch timeout after a delay
            handlerTimeout.postDelayed(runnableTimeout, TIMEOUT);

            super.onPostExecute(result);
        }
    }

    /**
     * The task that ping an ip, with increasing time to live (ttl) value
     */
    private class ExecutePingAsyncTask extends AsyncTask<Void, Void, String> {

        private boolean isCancelled;

        private String urlToPing;

        private int currentWhat;

        private boolean isfinished = false;

        public ExecutePingAsyncTask(String url, int what) {
            this.currentWhat = what;
            this.urlToPing = url;
        }

        /**
         * Launches the ping, launches InetAddress to retrieve url if there is
         * one, store trace
         */
        @Override
        protected String doInBackground(Void... params) {
            String res = "";
            if (hasConnectivity()) {
                try {
                    res = launchPing(urlToPing);
                    Log.d(TAG, "res-->" + res);
                    int loss = parseLossFromPing(res);
                    int delay = parseAverTimeFromPing(res);
                    // TracerouteContainer trace;
                    if (mOnTraceRouteListener != null) {
                        mOnTraceRouteListener
                                .onResult(currentWhat, loss, delay);
                    }

                } catch (final Exception e) {
                    if (mOnTraceRouteListener != null) {
                        mOnTraceRouteListener.onException(currentWhat);
                    }
                    e.printStackTrace();
                }
                isfinished = true;
            } else {
                if (mOnTraceRouteListener != null) {
                    mOnTraceRouteListener.onException(currentWhat);
                }
            }
            return res;
        }

        /**
         * Launches ping command
         *
         * @param url The url to ping
         * @return The ping string
         */
        @SuppressLint("NewApi")
        private String launchPing(String url) throws Exception {
            // Build ping command with parameters
            Process p;
            String command = "ping -c 1 ";

            Log.d(TAG, "Will launch : " + command + url);

            // timeout task
            new TimeOutAsyncTask(this, currentWhat).execute();
            // Launch command
            p = Runtime.getRuntime().exec(command + url);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            // Construct the response from ping
            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "\n";
            }

            p.destroy();

            if (res.equals("")) {
                throw new IllegalArgumentException();
            }

            return res;
        }

        /**
         * Treat the previous ping (launches a ttl+1 if it is not the final ip,
         * refresh the list on view etc...)
         */
        @Override
        protected void onPostExecute(String result) {
            if (!isCancelled) {
                try {
                    if (!"".equals(result)) {

                    }
                    finishedTasks++;
                } catch (final Exception e) {
                    // TODO
                    // context.runOnUiThread(new Runnable() {
                    // @Override
                    // public void run() {
                    // onException(e);
                    // }
                    // });
                }
            }

            super.onPostExecute(result);
        }

        /**
         * Handles exception on ping
         *
         * @param e The exception thrown
         */
        @SuppressWarnings("unused")
        private void onException(Exception e) {

        }

        public void setCancelled(boolean isCancelled) {
            this.isCancelled = isCancelled;
        }

    }

    /**
     * Gets the ip from the string returned by a ping
     *
     * @param ping The string returned by a ping command
     * @return The ip contained in the ping
     */
    @SuppressWarnings("unused")
    private String parseIpFromPing(String ping) {
        String ip = "";
        if (ping.contains(FROM_PING)) {
            // Get ip when ttl exceeded
            int index = ping.indexOf(FROM_PING);

            ip = ping.substring(index + 5);
            if (ip.contains(PARENTHESE_OPEN_PING)) {
                // Get ip when in parenthese
                int indexOpen = ip.indexOf(PARENTHESE_OPEN_PING);
                int indexClose = ip.indexOf(PARENTHESE_CLOSE_PING);

                ip = ip.substring(indexOpen + 1, indexClose);
            } else {
                // Get ip when after from
                ip = ip.substring(0, ip.indexOf("\n"));
                if (ip.contains(":")) {
                    index = ip.indexOf(":");
                } else {
                    index = ip.indexOf(" ");
                }

                ip = ip.substring(0, index);
            }
        } else {
            // Get ip when ping succeeded
            int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
            int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

            ip = ping.substring(indexOpen + 1, indexClose);
        }

        return ip;
    }

    private int parseLossFromPing(String ping) {
        int index = ping.indexOf(RECEIVED);
        String loss = ping.substring(index + 10);
        int indexClose = loss.indexOf(PERCENT);
        loss = loss.substring(0, indexClose);
        Log.d(TAG, "loss-->" + loss);
        return Integer.valueOf(loss);
    }

    private int parseAverTimeFromPing(String ping) {
        int index = ping.indexOf(RTT);
        String time = ping.substring(index);
        index = time.indexOf(EQUAL);
        time = time.substring(index);
        index = time.indexOf("/");
        time = time.substring(index + 1);
        int indexClose = time.indexOf("/");
        time = time.substring(0, indexClose);
        Log.d(TAG, "time-->" + time);
        return Float.valueOf(time).intValue();
    }

    /**
     * Gets the final ip we want to ping (example: if user fullfilled google.fr,
     * final ip could be 8.8.8.8)
     *
     * @param ping The string returned by a ping command
     * @return The ip contained in the ping
     */
    @SuppressWarnings("unused")
    private String parseIpToPingFromPing(String ping) {
        String ip = "";
        if (ping.contains(PING)) {
            // Get ip when ping succeeded
            int indexOpen = ping.indexOf(PARENTHESE_OPEN_PING);
            int indexClose = ping.indexOf(PARENTHESE_CLOSE_PING);

            ip = ping.substring(indexOpen + 1, indexClose);
        }

        return ip;
    }

    /**
     * Gets the time from ping command (if there is)
     *
     * @param ping The string returned by a ping command
     * @return The time contained in the ping
     */
    @SuppressWarnings("unused")
    private String parseTimeFromPing(String ping) {
        String time = "";
        if (ping.contains(TIME_PING)) {
            int index = ping.indexOf(TIME_PING);

            time = ping.substring(index + 5);
            index = time.indexOf(" ");
            time = time.substring(0, index);
        }

        return time;
    }

    /**
     * Check for connectivity (wifi and mobile)
     *
     * @return true if there is a connectivity, false otherwise
     */
    public boolean hasConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setOnTraceRouteListener(
            OnTraceRouteListener onTraceRouteListener) {
        mOnTraceRouteListener = onTraceRouteListener;
    }
}
