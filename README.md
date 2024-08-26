# FastSync

## Overview

This Kotlin Multiplatform project simplifies the process of sharing files between mobile and desktop devices using ADB (Android Debug Bridge). The primary goal is to eliminate the need to manually navigate through directories of your mobile device on desktop to find and transfer files. This tool accelerates the process by allowing you to add files directly from the clients on either platforms and then seamlessly transfer them to your other platform.

## Demo -->
[Screencast from 2024-08-26 08-03-01.webm](https://github.com/user-attachments/assets/7d6e3e73-8b1f-4db0-bf04-b053a52bd69b)

## Problem Solved

Manually browsing through your mobile device's directories from your computer can be time-consuming and slow due to indexing. Moreover, the traditional copy-paste method can be cumbersome, especially when dealing with multiple files. This project provides a streamlined solution:

- Easily select and add files you wish to transfer.
- Go to other client and refresh to view added files and click to download them directly to your destination.
- Can choose custom path.

## How It Works

1. **Adding Files:**  
   Files can be added from either the mobile or desktop client:
   - **Mobile Client:** Select and add files to be shared with the desktop.
   - **Desktop Client:** Add files to be shared with the mobile.

2. **File Storage:**  
   Once a file is added, it is stored on the local running server ([FastSync server](https://github.com/parneet-guraya/FastSync-server)) and recorded in the SQLite database. This makes it accessible for querying by the other client.

3. **Querying Files:**  
   The client (mobile or desktop) that did not initiate the file addition can query the server to view the list of available files. 

4. **File Transfer:**  
   - **Mobile to Desktop Transfer:** When a file is selected for download on the desktop client, an ADB command is executed under the hood to pull data from mobile.
   - **Desktop to Mobile Transfer:** Since ADB is running on desktop only, the mobile client needs to tell the DesktopClient to push data for that it sends the share ID and the target location via WebSocket. The desktop client receives this data and initiates an ADB push command to transfer the file from the desktop to the specified location on the mobile device.

5. **WebSocket Communication:**  
   WebSocket is used for efficient communication between the mobile and desktop clients. It helps coordinate the file transfer by allowing the mobile client to send necessary details (such as share IDs and target locations) to the desktop client, which then handles the file transfer via ADB.

## Features

- **Cross-Platform Support:** Works on both mobile and desktop platforms, thanks to Kotlin Multiplatform.
- **Efficient File Transfer:** Skip the hassle of manual navigation and transfer files directly with minimal effort.
- **Optimized Performance:** By using ADB and direct command execution, the file transfer process is faster compared to traditional methods.

## Getting Started

### Prerequisites

- Kotlin Multiplatform setup
- ADB installed on your machine and add it global path
- Basic knowledge of how to run Kotlin projects

### Steps to Build 
- Make sure server is running
- In this project to build Desktop App execute this `./gradlew run`
- Build and run Android client
- Make sure both Desktop and Mobile connected to the same network (necessary to access server that is running locally)
- Add the local ip address
- Done!
