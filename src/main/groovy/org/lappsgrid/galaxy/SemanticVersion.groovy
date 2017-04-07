package org.lappsgrid.galaxy

class SemanticVersion implements Comparable<SemanticVersion> {
	int major
	int minor
	int revision
	String qualifier

    SemanticVersion(String input) {
		List parts = input.tokenize('.')
		if (parts.size() != 3) {
			throw Exception("Illegal version number")
		}
		major = parts[0] as int
		minor = parts[1] as int
		parts = parts[2].tokenize('-')
		revision = parts[0] as int
		if (parts.size() > 1) {
			qualifier = parts[1]
		}
	}

    SemanticVersion(int major, int minor, int revision) {
		this(major, minor, revision, null)
	}

    SemanticVersion(int major, int minor, int revision, String qualifier) {
		this.major = major
		this.minor = minor
		this.revision = revision
		this.qualifier = qualifier
	}
	
	String toString() {
		if (qualifier) {
			return "$major.$minor.$revision-$qualifier"
		}
		return "$major.$minor.$revision"
	}
	
	int compareTo(SemanticVersion other) {
		if (major > other.major) {
			return 1
		}
		else if (major < other.major) {
			return -1
		}
		else if (minor > other.minor) {
			return 1
		}
		else if (minor < other.minor) {
			return -1
		}
		else if (revision > other.revision) {
			return 1
		}
		else if (revision < other.revision) {
			return -1
		}
		else if (qualifier && other.qualifier) {
			return 0
		}
		else if (other.qualifier) {
			return 1
		}
		else if (qualifier) {
			return -1
		}
		return 0
	}
}