import './ProjectCard.css';

const ProjectCard = ({ project, onActivate, taskCount, onAddTask, onDelete }) => {
  const isActive = project.status === 'ACTIVE';
  const isDraft = project.status === 'DRAFT';

  return (
    <div className={`project-card ${isActive ? 'active' : 'draft'}`}>
      <div className="project-header">
        <h3>{project.name}</h3>
        <div className="header-actions">
          <span className={`status-badge ${isActive ? 'active' : 'draft'}`}>
            {project.status}
          </span>
          <button onClick={() => onDelete(project.id)} className="btn-delete" title="Delete project">
            🗑️
          </button>
        </div>
      </div>
      <div className="project-footer">
        <span className="task-count">📋 {taskCount} tasks</span>
        <div className="project-actions">
          <button onClick={onAddTask} className="btn-add-task">
            + Add Task
          </button>
          {isDraft && taskCount > 0 && (
            <button onClick={() => onActivate(project.id)} className="btn-activate">
              Activate
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProjectCard;
