package com.example.data.mock

import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.ResourceItem
import com.example.data.model.ResourceType
import com.example.data.model.TechnicalCategory

object MockData {

    val technicalCategories = listOf(
        TechnicalCategory(
            id = "web_dev",
            name = "Web Development",
            fileCount = 248,
            iconName = "globe",
            subtopics = listOf("HTML", "CSS", "JavaScript", "React", "Node.js", "Express", "APIs", "Git & GitHub")
        ),
        TechnicalCategory(
            id = "app_dev",
            name = "Android Development",
            fileCount = 196,
            iconName = "phone",
            subtopics = listOf("Android Basics", "Kotlin", "Android Studio", "Jetpack Compose", "Activities", "Architecture Components", "APIs", "Firebase")
        ),
        TechnicalCategory(
            id = "dsa",
            name = "DSA",
            fileCount = 312,
            iconName = "tree",
            subtopics = listOf("Arrays", "Strings", "Linked List", "Stack & Queue", "Trees", "Graphs", "Sorting & Searching", "Dynamic Programming")
        ),
        TechnicalCategory(
            id = "java",
            name = "Java",
            fileCount = 243,
            iconName = "coffee",
            subtopics = listOf("OOP Concepts", "Collections Framework", "Multithreading", "Exception Handling", "Streams & Lambdas", "JVM Internals")
        ),
        TechnicalCategory(
            id = "python",
            name = "Python",
            fileCount = 217,
            iconName = "terminal",
            subtopics = listOf("Core Python", "Data Structures", "OOP in Python", "NumPy & Pandas", "Web Scraping", "Flask & FastAPI")
        ),
        TechnicalCategory(
            id = "cpp",
            name = "C++",
            fileCount = 189,
            iconName = "code",
            subtopics = listOf("Basics & Pointers", "OOP in C++", "STL Containers", "Templates & Generic Programming", "Memory Management", "Competitive Programming Tricks")
        ),
        TechnicalCategory(
            id = "aiml",
            name = "AI & ML",
            fileCount = 164,
            iconName = "brain",
            subtopics = listOf("Math for ML", "Supervised Learning", "Unsupervised Learning", "Deep Learning & PyTorch", "NLP", "Computer Vision")
        ),
        TechnicalCategory(
            id = "dbms",
            name = "Database (DBMS)",
            fileCount = 152,
            iconName = "database",
            subtopics = listOf("ER Diagrams", "Relational Algebra", "SQL Queries", "Normalization 1NF-BCNF", "Transactions & ACID", "Indexing & B-Trees")
        ),
        TechnicalCategory(
            id = "os",
            name = "Operating Systems",
            fileCount = 140,
            iconName = "cpu",
            subtopics = listOf("Processes & Threads", "CPU Scheduling", "Synchronization & Mutex", "Deadlock Avoidance", "Paging & Virtual Memory", "File Systems")
        ),
        TechnicalCategory(
            id = "cn",
            name = "Computer Networks",
            fileCount = 128,
            iconName = "network",
            subtopics = listOf("OSI & TCP/IP Reference Models", "Data Link Layer & Framing", "IP Addressing & Subnetting", "Routing Protocols (OSPF/BGP)", "TCP vs UDP", "Network Security")
        )
    )

    val initialResources = listOf(
        // Web Development Notes & PYQs
        ResourceItem(
            id = "res-1",
            title = "HTML Complete Notes & Cheatsheet",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Web Development",
            semester = 3,
            subject = "Web Technologies",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "2.4 MB",
            pageCount = 48,
            uploadedDate = "Oct 2025",
            subtopic = "HTML",
            description = "Detailed HTML5 semantic elements, forms, accessibility attributes, SEO best practices, and interview questions.",
            isBookmarked = true,
            samplePreview = """
# HTML5 Complete Study Guide
## 1. Introduction to Web Architecture
- Client-Server HTTP/HTTPS request lifecycle
- DNS resolution, TCP 3-way handshake, TLS termination
- DOM (Document Object Model) tree generation

## 2. Semantic Elements
- `<header>`, `<nav>`, `<main>`, `<article>`, `<section>`, `<footer>`
- Benefits: Screen-readers, search engine crawlers, structured maintenance

## 3. Web Forms & Validation
- Input types: email, url, number, date, regex patterns
- ARIA accessibility roles and tab-indexing standards
            """.trimIndent()
        ),
        ResourceItem(
            id = "res-2",
            title = "CSS Modern Layouts & Flexbox/Grid",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Web Development",
            semester = 3,
            subject = "Web Technologies",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "1.8 MB",
            pageCount = 36,
            uploadedDate = "Sep 2025",
            subtopic = "CSS",
            description = "Master Flexbox, CSS Grid, Responsive Media Queries, Box Model, and CSS Variables."
        ),
        ResourceItem(
            id = "res-3",
            title = "JavaScript ES6+ Deep Dive Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Web Development",
            semester = 4,
            subject = "Web Technologies",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "3.2 MB",
            pageCount = 64,
            uploadedDate = "Aug 2025",
            subtopic = "JavaScript",
            description = "Event loop, closures, promises, async/await, prototypes, and practical DOM manipulation."
        ),
        ResourceItem(
            id = "res-4",
            title = "React.js Component Architecture Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Web Development",
            semester = 5,
            subject = "Web Technologies",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "2.7 MB",
            pageCount = 52,
            uploadedDate = "Sep 2025",
            subtopic = "React",
            description = "Hooks (useState, useEffect, useMemo, useCallback), Virtual DOM, Context API, and state management."
        ),
        ResourceItem(
            id = "res-5",
            title = "Web Technologies — End Semester PYQ 2024",
            type = ResourceType.PYQ,
            category = "Web Development",
            semester = 5,
            subject = "Web Technologies",
            year = "2024",
            examType = "End Semester",
            fileSize = "4.1 MB",
            pageCount = 8,
            uploadedDate = "Jul 2024",
            description = "Official university end-term question paper with verified answer keys and marking scheme."
        ),
        ResourceItem(
            id = "res-6",
            title = "Web Technologies — Sessional Exam 1 & 2 2025",
            type = ResourceType.SESSIONAL,
            category = "Web Development",
            semester = 5,
            subject = "Web Technologies",
            year = "2025",
            examType = "Mid Term Sessional",
            fileSize = "2.0 MB",
            pageCount = 6,
            uploadedDate = "Feb 2025",
            description = "Combined mid-term sessional exam papers including client-side scripting and XML/JSON questions."
        ),

        // Android Development
        ResourceItem(
            id = "res-7",
            title = "Kotlin & Jetpack Compose Master Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Android Development",
            semester = 6,
            subject = "Mobile Application Development",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "2.8 MB",
            pageCount = 58,
            uploadedDate = "Oct 2025",
            subtopic = "Jetpack Compose",
            description = "Modern declarative UI, state hoisting, ViewModel integration, Coroutines, and Navigation Compose."
        ),
        ResourceItem(
            id = "res-8",
            title = "Mobile Application Dev — End Sem PYQ 2024",
            type = ResourceType.PYQ,
            category = "Android Development",
            semester = 6,
            subject = "Mobile Computing",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.4 MB",
            pageCount = 10,
            uploadedDate = "Jun 2024",
            description = "Previous year university paper on Android Lifecycle, Services, Broadcast Receivers, and SQLite."
        ),

        // DSA
        ResourceItem(
            id = "res-9",
            title = "Data Structures & Algorithms — Unit 1 & 2 Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "DSA",
            semester = 3,
            subject = "Data Structures",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "4.1 MB",
            pageCount = 76,
            uploadedDate = "Sep 2025",
            subtopic = "Arrays",
            description = "Asymptotic analysis, Arrays, Linked Lists, Stacks, Queues, recursion and pointer mechanics.",
            isBookmarked = true
        ),
        ResourceItem(
            id = "res-10",
            title = "Trees & Graphs Complete Problem Set & Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "DSA",
            semester = 3,
            subject = "Data Structures",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "3.8 MB",
            pageCount = 60,
            uploadedDate = "Oct 2025",
            subtopic = "Trees",
            description = "Binary Search Trees, AVL Trees, B-Trees, Graph traversals (BFS, DFS), Dijkstra, and Kruskal algorithms."
        ),
        ResourceItem(
            id = "res-11",
            title = "Data Structures — 3rd Semester End Sem PYQ 2024",
            type = ResourceType.PYQ,
            category = "DSA",
            semester = 3,
            subject = "Data Structures",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.1 MB",
            pageCount = 12,
            uploadedDate = "Jan 2024",
            description = "End semester question paper with solutions for tree rotations, topological sort, and time complexities."
        ),
        ResourceItem(
            id = "res-12",
            title = "Data Structures — Sessional Exam Papers 2024-2025",
            type = ResourceType.SESSIONAL,
            category = "DSA",
            semester = 3,
            subject = "Data Structures",
            year = "2025",
            examType = "Sessional 1 & 2",
            fileSize = "2.2 MB",
            pageCount = 8,
            uploadedDate = "Mar 2025",
            description = "Internal college sessional examination papers with numerical step-by-step solutions."
        ),

        // Java
        ResourceItem(
            id = "res-13",
            title = "Java OOP & Collections Framework Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Java",
            semester = 4,
            subject = "Object Oriented Programming",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "3.2 MB",
            pageCount = 54,
            uploadedDate = "Aug 2025",
            subtopic = "OOP Concepts",
            description = "Inheritance, Polymorphism, Interfaces, Abstract Classes, ArrayList, HashMap, and Generics."
        ),
        ResourceItem(
            id = "res-14",
            title = "Java Programming — End Sem PYQ 2023-2024",
            type = ResourceType.PYQ,
            category = "Java",
            semester = 4,
            subject = "Object Oriented Programming",
            year = "2024",
            examType = "End Semester",
            fileSize = "2.9 MB",
            pageCount = 10,
            uploadedDate = "May 2024",
            description = "University questions with code explanations for multithreading, exception hierarchy, and JDBC."
        ),

        // Python
        ResourceItem(
            id = "res-15",
            title = "Python Programming — Important Question Bank",
            type = ResourceType.TECHNICAL_NOTES,
            category = "Python",
            semester = 2,
            subject = "Programming for Problem Solving",
            year = "2025",
            examType = "Question Bank",
            fileSize = "2.5 MB",
            pageCount = 44,
            uploadedDate = "Sep 2025",
            subtopic = "Core Python",
            description = "List comprehensions, dictionary operations, file I/O, regex, and university examination model answers."
        ),
        ResourceItem(
            id = "res-16",
            title = "Python — Sessional Exam Papers (All Sets)",
            type = ResourceType.SESSIONAL,
            category = "Python",
            semester = 2,
            subject = "Programming in Python",
            year = "2024",
            examType = "Mid Term Sessional",
            fileSize = "1.9 MB",
            pageCount = 7,
            uploadedDate = "Dec 2024",
            description = "Set A, B, and C sessional question papers with sample inputs, outputs, and trace tables."
        ),

        // C++
        ResourceItem(
            id = "res-17",
            title = "C++ & STL Comprehensive Handwritten Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "C++",
            semester = 2,
            subject = "Problem Solving through C++",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "3.6 MB",
            pageCount = 68,
            uploadedDate = "Aug 2025",
            subtopic = "STL Containers",
            description = "Vector, map, set, priority_queue, iterators, lambda expressions, and memory pointers."
        ),
        ResourceItem(
            id = "res-18",
            title = "C++ Sessional Exam Papers (2021 - 2024)",
            type = ResourceType.SESSIONAL,
            category = "C++",
            semester = 2,
            subject = "Object Oriented C++",
            year = "2024",
            examType = "Sessional",
            fileSize = "1.9 MB",
            pageCount = 9,
            uploadedDate = "Nov 2024",
            description = "Mid term question sets focusing on constructor chaining, operator overloading, and virtual functions."
        ),

        // AI & ML
        ResourceItem(
            id = "res-19",
            title = "Machine Learning Algorithms & Math Notes",
            type = ResourceType.TECHNICAL_NOTES,
            category = "AI & ML",
            semester = 7,
            subject = "Artificial Intelligence",
            year = "2025",
            examType = "Comprehensive Notes",
            fileSize = "4.5 MB",
            pageCount = 82,
            uploadedDate = "Oct 2025",
            subtopic = "Supervised Learning",
            description = "Linear regression, Logistic regression, SVM, Decision Trees, Random Forests, Gradient Boosting, Loss functions."
        ),
        ResourceItem(
            id = "res-20",
            title = "Artificial Intelligence — End Sem PYQ 2024",
            type = ResourceType.PYQ,
            category = "AI & ML",
            semester = 7,
            subject = "Artificial Intelligence",
            year = "2024",
            examType = "End Semester",
            fileSize = "2.8 MB",
            pageCount = 8,
            uploadedDate = "May 2024",
            description = "State space search, A* heuristic, minimax with alpha-beta pruning, and resolution refutation."
        ),

        // Semester 1 & 2 PYQs
        ResourceItem(
            id = "res-21",
            title = "Engineering Mathematics-I — End Sem PYQ (2020-2024)",
            type = ResourceType.PYQ,
            category = "Academic",
            semester = 1,
            subject = "Engineering Mathematics-I",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.7 MB",
            pageCount = 18,
            uploadedDate = "Jan 2025",
            description = "Matrices, Cayley-Hamilton Theorem, Partial Differentiation, Multiple Integrals, Vector Calculus."
        ),
        ResourceItem(
            id = "res-22",
            title = "Engineering Physics — Sessional Exam Papers",
            type = ResourceType.SESSIONAL,
            category = "Academic",
            semester = 1,
            subject = "Engineering Physics",
            year = "2024",
            examType = "Sessional",
            fileSize = "2.1 MB",
            pageCount = 6,
            uploadedDate = "Nov 2024",
            description = "Interference, Diffraction, Lasers, Fiber Optics, Quantum Mechanics."
        ),

        // Semester 5 & 6 Core subjects (DBMS, OS, CN)
        ResourceItem(
            id = "res-23",
            title = "Database Management Systems (DBMS) — End Sem 2024",
            type = ResourceType.PYQ,
            category = "Database (DBMS)",
            semester = 5,
            subject = "DBMS",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.5 MB",
            pageCount = 14,
            uploadedDate = "Jun 2024",
            description = "Normalization (BCNF, 3NF), Concurrency Control, Two Phase Locking, SQL subqueries."
        ),
        ResourceItem(
            id = "res-24",
            title = "Operating Systems — End Sem PYQ 2024",
            type = ResourceType.PYQ,
            category = "Operating Systems",
            semester = 4,
            subject = "Operating Systems",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.2 MB",
            pageCount = 12,
            uploadedDate = "May 2024",
            description = "Banker's Algorithm, Page Replacement (LRU, FIFO), Semaphores, Disk Scheduling."
        ),
        ResourceItem(
            id = "res-25",
            title = "Operating Systems — Sessional Exam 2025",
            type = ResourceType.SESSIONAL,
            category = "Operating Systems",
            semester = 4,
            subject = "Operating Systems",
            year = "2025",
            examType = "Sessional",
            fileSize = "1.8 MB",
            pageCount = 5,
            uploadedDate = "Mar 2025",
            description = "Process scheduling algorithms, critical section problems, and fork() system call trace."
        ),
        ResourceItem(
            id = "res-26",
            title = "Computer Networks — End Sem PYQ 2024",
            type = ResourceType.PYQ,
            category = "Computer Networks",
            semester = 6,
            subject = "Computer Networks",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.9 MB",
            pageCount = 14,
            uploadedDate = "Jun 2024",
            description = "Subnet masking, Distance Vector & Link State Routing, TCP flow control & congestion control."
        ),
        ResourceItem(
            id = "res-27",
            title = "Cloud Computing & DevOps — Sessional 2025",
            type = ResourceType.SESSIONAL,
            category = "Academic",
            semester = 7,
            subject = "Cloud Computing",
            year = "2025",
            examType = "Sessional",
            fileSize = "2.3 MB",
            pageCount = 8,
            uploadedDate = "Feb 2025",
            description = "Virtualization, Docker containerization, AWS EC2/S3 architecture, CI/CD pipeline principles."
        ),
        ResourceItem(
            id = "res-28",
            title = "Information Security & Cryptography PYQ 2024",
            type = ResourceType.PYQ,
            category = "Academic",
            semester = 8,
            subject = "Information Security",
            year = "2024",
            examType = "End Semester",
            fileSize = "3.0 MB",
            pageCount = 10,
            uploadedDate = "May 2024",
            description = "RSA, AES, Diffie-Hellman Key Exchange, Digital Signatures, Firewall architectures."
        )
    )

    val initialDownloads = listOf(
        DownloadItem(
            id = "down-1",
            resourceId = "res-1",
            title = "Web Development Notes",
            category = "Web Development",
            type = ResourceType.TECHNICAL_NOTES,
            fileSize = "2.4 MB",
            status = DownloadStatus.COMPLETED,
            progress = 1.0f
        ),
        DownloadItem(
            id = "down-2",
            resourceId = "res-11",
            title = "DSA 3rd Sem PYQs",
            category = "DSA",
            type = ResourceType.PYQ,
            fileSize = "4.1 MB",
            status = DownloadStatus.COMPLETED,
            progress = 1.0f
        ),
        DownloadItem(
            id = "down-3",
            resourceId = "res-13",
            title = "Java Notes",
            category = "Java",
            type = ResourceType.TECHNICAL_NOTES,
            fileSize = "3.2 MB",
            status = DownloadStatus.COMPLETED,
            progress = 1.0f
        ),
        DownloadItem(
            id = "down-4",
            resourceId = "res-7",
            title = "App Development Notes",
            category = "Android Development",
            type = ResourceType.TECHNICAL_NOTES,
            fileSize = "2.8 MB",
            status = DownloadStatus.DOWNLOADING,
            progress = 0.68f
        ),
        DownloadItem(
            id = "down-5",
            resourceId = "res-15",
            title = "Python Notes",
            category = "Python",
            type = ResourceType.TECHNICAL_NOTES,
            fileSize = "2.5 MB",
            status = DownloadStatus.PENDING,
            progress = 0.15f
        ),
        DownloadItem(
            id = "down-6",
            resourceId = "res-18",
            title = "C++ Sessional Papers",
            category = "C++",
            type = ResourceType.SESSIONAL,
            fileSize = "1.9 MB",
            status = DownloadStatus.PENDING,
            progress = 0.0f
        )
    )
}
