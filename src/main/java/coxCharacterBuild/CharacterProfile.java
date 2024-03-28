package coxCharacterBuild;

public class CharacterProfile {
	private String name;
	private String architype;
	private String architypeIcon;
	private String alignment;
	private String alignmentIcon;
	private String level;
	private String origin;
	private String originIcon;
	private String primary;
	private String secondary;
	private String filename;
	private String title;
	private String lastActive;
	public CharacterProfile(String name, 
			String architype,
			String architypeIcon,
			String alignment,
			String alignmentIcon,
			String level,
			String origin,
			String originIcon, 
			String primary, 
			String secondary, 
			String filename, 
			String title,
			String lastActive) {
		this.name = name;
		this.architype = architype;
		this.architypeIcon = architypeIcon;
		this.alignment = alignment;
		this.alignmentIcon = alignmentIcon;
		this.level = level;
		this.origin = origin;
		this.originIcon = originIcon;
		this.primary = primary;
		this.secondary = secondary;
		this.filename = filename;
		this.title = title;
		this.lastActive = lastActive;
	}
	public CharacterProfile(String name,
			String architype,
			String architypeIcon,
			String alignment,
			String alignmentIcon,
			int level,
			String origin,
			String originIcon,
			String primary,
			String secondary,
			String filename,
			String title,
			String lastActive) {
		this.name = name;
		this.architype = architype;
		this.architypeIcon = architypeIcon;
		this.alignment = alignment;
		this.alignmentIcon = alignmentIcon;
		this.level = String.valueOf(level);
		this.origin = origin;
		this.originIcon = originIcon;
		this.primary = primary;
		this.secondary = secondary;
		this.filename = filename;
		this.title = title;
		this.lastActive = lastActive;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getArchitype() {
		return architype;
	}
	public void setArchitype(String architype) {
		this.architype = architype;
	}
	public String getArchitypeIcon() {
		return architypeIcon;
	}
	public void setArchitypeIcon(String architypeIcon) {
		this.architypeIcon = architypeIcon;
	}
	public String getAlignment() {
		return alignment;
	}
	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}
	public String getAlignmentIcon() {
		return alignmentIcon;
	}
	public void setAlignmentIcon(String alignmentIcon) {
		this.alignmentIcon = alignmentIcon;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getOriginIcon() {
		return originIcon;
	}
	public void setOriginIcon(String originIcon) {
		this.originIcon = originIcon;
	}
	public String getPrimary() {
		return primary;
	}
	public void setPrimary(String primary) {
		this.primary = primary;
	}
	public String getSecondary() {
		return secondary;
	}
	public void setSecondary(String secondary) {
		this.secondary = secondary;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLastActive() {
		return lastActive;
	}
	public void setLastActive(String lastActive) {
		this.lastActive = lastActive;
	}
}
